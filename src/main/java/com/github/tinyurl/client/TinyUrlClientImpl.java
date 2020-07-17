package com.github.tinyurl.client;

import com.github.tinyurl.client.config.HttpPoolConfig;
import com.github.tinyurl.client.config.TinyUrlClientConfig;
import com.github.tinyurl.client.constant.Constants;
import com.github.tinyurl.client.constant.ErrorCode;
import com.github.tinyurl.client.entity.Response;
import com.github.tinyurl.client.entity.TinyUrlObject;
import com.github.tinyurl.client.entity.TinyUrlParam;
import com.github.tinyurl.client.exception.TinyUrlException;
import com.github.tinyurl.client.http.RestClient;
import com.github.tinyurl.client.loadbalancer.BaseLoadBalancer;
import com.github.tinyurl.client.loadbalancer.LoadBalancer;
import com.github.tinyurl.client.loadbalancer.Server;
import com.github.tinyurl.client.util.NumberUtil;
import com.github.tinyurl.client.util.ObjectUtil;
import com.github.tinyurl.client.util.SignUtil;
import com.github.tinyurl.client.util.StringUtil;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 短链接客户端
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/15
 */
public class TinyUrlClientImpl implements TinyUrlClient {

    private static final String API_SHORTEN = "/shorten";

    private final TinyUrlClientConfig clientConfig;
    private final HttpPoolConfig httpPoolConfig;
    private final LoadBalancer loadBalancer;
    private RestClient restClient;

    public TinyUrlClientImpl(final TinyUrlClientConfig clientConfig,
                             final HttpPoolConfig httpPoolConfig) {
        this.clientConfig = clientConfig;
        this.httpPoolConfig = httpPoolConfig;
        loadBalancer = new BaseLoadBalancer();
    }

    @Override
    public void initialize() {
        if (StringUtil.isEmpty(clientConfig.getHost())) {
            throw new TinyUrlException(ErrorCode.CLIENT_HOST_NOT_CONFIGURE);
        }

        // 解析服务
        String[] servers = clientConfig.getHost().split(Constants.SEMICOLON);
        for (String s : servers) {
            String scheme;
            String hostPort;
            if (s.toLowerCase().startsWith(Constants.HTTP_SCHEMA)) {
                scheme = Constants.HTTP_SCHEMA;
                hostPort = s.substring(scheme.length());
            } else if (s.startsWith(Constants.HTTPS_SCHEMA)) {
                scheme = Constants.HTTPS_SCHEMA;
                hostPort = s.substring(scheme.length());
            } else {
                // 默认方案为http
                scheme = Constants.HTTPS_SCHEMA;
                hostPort = s;
            }

            String[] hps = hostPort.split(":");
            if (hps.length != 2) {
                throw new TinyUrlException(ErrorCode.CLIENT_HOST_FORMAT_ERROR);
            }

            if (!NumberUtil.isNumber(hps[1])) {
                throw new TinyUrlException(ErrorCode.CLIENT_HOST_FORMAT_ERROR);
            }

            int port = Integer.parseInt(hps[1]);

            Server server = new Server(hps[0], port, scheme);

            loadBalancer.addServer(server);

            restClient = new RestClient(httpPoolConfig, loadBalancer);
            restClient.initialize();
        }
    }

    @Override
    public TinyUrlObject shorten(TinyUrlParam tinyUrlParam) {
        Server server = loadBalancer.chooseServer(null);
        if (ObjectUtil.isNull(server)) {
            throw new TinyUrlException(ErrorCode.SYSTEM_ERROR);
        }

        // 组装请求参数
        Map<String, String> requestParam = new TreeMap<>();
        requestParam.put("url", tinyUrlParam.getUrl());
        requestParam.put("type", "DECIMAL");
        requestParam.put("domain", tinyUrlParam.getDomain());
        requestParam.put("timestamp", String.valueOf(System.currentTimeMillis()));
        requestParam.put("appId", clientConfig.getAppId());
        requestParam.put("nonceStr", UUID.randomUUID().toString());
        // 生成签名
        requestParam.put("sign", SignUtil.sign(requestParam, clientConfig.getKey()));

        // 发起请求
        Response response = restClient.postForObject(server.getUrl() + API_SHORTEN, requestParam, Response.class);
        if (ObjectUtil.isNull(response) || ErrorCode.SUCCESS.getCode() != response.getCode()) {
            throw new TinyUrlException(ErrorCode.CLIENT_REMOTE_CALL_ERROR);
        }

        if (ErrorCode.SUCCESS.getCode() != response.getCode()) {
            throw new TinyUrlException(ErrorCode.CLIENT_REMOTE_CALL_ERROR);
        }

        String shortUrl = (String) response.getData();

        // 组装Object 返回
        TinyUrlObject object = new TinyUrlObject();
        object.setUrl(shortUrl);
        return object;
    }
}
