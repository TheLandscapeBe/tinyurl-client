package com.github.tinyurl.client.http;

import com.alibaba.fastjson.JSON;
import com.github.tinyurl.client.config.HttpPoolConfig;
import com.github.tinyurl.client.util.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.activation.MimeType;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * apache http客户端连接池
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/17
 */
@Slf4j
public class HttpClientPool {
    private static final long HTTP_CLIENT_LOCK_TIMEOUT = 30L * 1000;
    private final Lock httpClientLock = new ReentrantLock();
    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";
    private volatile CloseableHttpClient httpClient = null;
    private final HttpPoolConfig httpPoolConfig;
    private final ThreadFactory evictHttpClientFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "evict_http_client");
        }
    };

    private IdleConnectionEvictor idleConnectionEvictor;

    public HttpClientPool(HttpPoolConfig httpPoolConfig) {
        this.httpPoolConfig = httpPoolConfig;
    }

    private void configHttpMethod(HttpRequestBase httpRequestBase) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(httpPoolConfig.getConnectTimeout())
                .setConnectTimeout(httpPoolConfig.getConnectTimeout())
                .setSocketTimeout(httpPoolConfig.getReadTimeout()).build();
        httpRequestBase.setConfig(requestConfig);
    }

    public CloseableHttpClient getHttpClient(String url) throws InterruptedException {
        InetSocketAddress socketAddress = UrlUtil.socketAddress(url);

        if (httpClientLock.tryLock(HTTP_CLIENT_LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
            if (httpClient == null) {
                httpClient = createHttpClient(httpPoolConfig.getMaxTotal(),
                        httpPoolConfig.getDefaultMaxRoute(),
                        httpPoolConfig.getMaxPerRoute(),
                        socketAddress.getHostName(),
                        socketAddress.getPort());
            }
            return httpClient;
        }

        return null;
    }

    public CloseableHttpClient createHttpClient(int maxTotal,
                                                       int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory socketFactory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register(SCHEME_HTTP, socketFactory)
                .register(SCHEME_HTTPS, sslSocketFactory).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        HttpRequestRetryHandler retryHandler = getHttpRequestRetryHandler(httpPoolConfig.getRetryTimes());

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(retryHandler).build();

        idleConnectionEvictor = new IdleConnectionEvictor(cm, evictHttpClientFactory,
                httpPoolConfig.getEvictInterval(), TimeUnit.MILLISECONDS,
                httpPoolConfig.getEvictInterval(), TimeUnit.MILLISECONDS);
        idleConnectionEvictor.start();
        return httpClient;
    }

    public String post(String url, Object param) throws IOException, InterruptedException {
        HttpPost httpPost = new HttpPost(url);
        configHttpMethod(httpPost);
        setPostParams(httpPost, param);
        return request(url, httpPost);
    }

    public String get(String url, Map<String, String> params) throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(url);
        configHttpMethod(httpGet);
        setGetParams(httpGet, params);
        return request(url, httpGet);
    }

    private String request(String url, HttpRequestBase httpRequest) throws IOException, InterruptedException {
        try(CloseableHttpResponse response = getHttpClient(url).execute(httpRequest, HttpClientContext.create())) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);
            return result;
        }
    }

    private static void setGetParams(HttpGet httpGet, Map<String, String> params) throws IOException {
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            params.forEach((key, value) -> {
                if (key != null && value != null) {
                    nameValuePairs.add(new BasicNameValuePair(key, value));
                }
            });

            try {
                String str = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
                String uri = httpGet.getURI().toString();
                if (uri.contains("?")) {
                    httpGet.setURI(new URI(httpGet.getURI().toString() + "&" + str));
                } else {
                    httpGet.setURI(new URI(httpGet.getURI().toString() + "?" + str));
                }
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    private void setPostParams(HttpEntityEnclosingRequestBase httpEntityRequest,
                               Object params) {
        String json = JSON.toJSONString(params);
        httpEntityRequest.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpEntityRequest.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        httpEntityRequest.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
    }

    private HttpRequestRetryHandler getHttpRequestRetryHandler(final int retryTimes) {
        HttpRequestRetryHandler retryHandler;
        if (httpPoolConfig.getRetryTimes() > 0) {
            // 请求重试处理
            retryHandler = (exception, executionCount, context) -> {
                // 重试N次
                if (executionCount >= retryTimes) {
                    return false;
                }

                // 无响应重试
                if (exception instanceof NoHttpResponseException) {
                    return true;
                }

                // SSL握手异常，不重试
                if (exception instanceof SSLHandshakeException) {
                    return false;
                }

                // 超时
                if (exception instanceof InterruptedIOException) {
                    return false;
                }

                // 目标服务器不可达
                if (exception instanceof UnknownHostException) {
                    return false;
                }

                // 连接被拒绝
                if (exception instanceof ConnectTimeoutException) {
                    return false;
                }
                // SSL握手异常
                if (exception instanceof SSLException) {
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            };
        } else {
            retryHandler = new DefaultHttpRequestRetryHandler(httpPoolConfig.getRetryTimes(), httpPoolConfig.getRetryTimes() > 0);
        }

        return retryHandler;
    }
}
