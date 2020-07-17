package com.github.tinyurl.client.config;

import lombok.Builder;
import lombok.Data;

/**
 * http连接池配置
 *
 * @author jiquanxi
 * @date 2020/07/17
 */
@Data
@Builder
public class HttpPoolConfig {
    /**
     * 连接超时时间
     */
    private int connectTimeout = 60 * 1000;

    /**
     * 读取超时时间
     */
    private int readTimeout = 60 * 1000;

    /**
     * 最大连接数
     */
    private int maxTotal = 100;

    /**
     * 默认最大路由
     */
    private int defaultMaxRoute = 100;

    /**
     * 每个route最大连接数
     */
    private int maxPerRoute = 100;

    /**
     * 重试次数，默认为0
     */
    private int retryTimes = 0;

    /**
     * 链接资源释放周期，默认为1分钟
     */
    private int evictInterval = 60 * 1000;
}
