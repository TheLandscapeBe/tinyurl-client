package com.github.tinyurl.client.http;

import com.alibaba.fastjson.JSON;
import com.github.tinyurl.client.config.HttpPoolConfig;
import com.github.tinyurl.client.loadbalancer.LoadBalancer;
import com.github.tinyurl.client.util.ObjectUtil;
import com.github.tinyurl.client.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * http客户端接口
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/17
 */
@Slf4j
public class RestClient {
    private final HttpPoolConfig httpPoolConfig;

    private HttpClientPool httpClientPool;

    private RestTemplate restTemplate;

    private final LoadBalancer loadBalancer;

    public RestClient(HttpPoolConfig httpPoolConfig, LoadBalancer loadBalancer, RestTemplate restTemplate) {
        this.httpPoolConfig = httpPoolConfig;
        this.loadBalancer = loadBalancer;
        this.restTemplate = restTemplate;
    }

    public void initialize() {
        httpClientPool = new HttpClientPool(httpPoolConfig);
    }

    /**
     * POST请求接口
     * @param url 接口URL
     * @param param 接口参数
     * @param classObj
     * @param <T>
     * @return
     */
    public <T> T postForObject(String url, Object param, Class<T> classObj) {
        if (ObjectUtil.isNotNull(restTemplate)) {
            return restTemplate.postForObject(url, param, classObj);
        } else {
            try {
                String result = httpClientPool.post(url, param);
                if (StringUtil.isNotEmpty(result)) {
                    return JSON.parseObject(result, classObj);
                }
            } catch (IOException | InterruptedException e) {
                log.error("error", e);
            }
        }

        return null;
    }
}
