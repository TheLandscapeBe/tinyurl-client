package com.github.tinyurl.client.http;

import com.alibaba.fastjson.JSON;
import com.github.tinyurl.client.config.HttpPoolConfig;
import com.github.tinyurl.client.loadbalancer.BaseLoadBalancer;
import com.github.tinyurl.client.loadbalancer.LoadBalancer;
import com.github.tinyurl.client.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

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

    private final LoadBalancer loadBalancer;

    public RestClient(HttpPoolConfig httpPoolConfig, LoadBalancer loadBalancer) {
        this.httpPoolConfig = httpPoolConfig;
        this.loadBalancer = loadBalancer;
    }

    public void initialize() {
        httpClientPool = new HttpClientPool(httpPoolConfig);
    }

    /**
     * POST请求接口
     * @param url 接口URL
     * @param params 接口参数
     * @param classObj
     * @param <T>
     * @return
     */
    public <T> T postForObject(String url, Object param, Class<T> classObj) {
        try {
            String result = httpClientPool.post(url, param);
            if (StringUtil.isNotEmpty(result)) {
                return JSON.parseObject(result, classObj);
            }
        } catch (IOException | InterruptedException e) {
            log.error("error", e);
        }

        return null;
    }
}
