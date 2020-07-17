package com.github.tinyurl.client.config;

import lombok.Builder;
import lombok.Data;

/**
 * 短链接客户端配置
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/15
 */
@Data
@Builder
public class TinyUrlClientConfig {
    /**
     * 域名,多个域名以逗号分割
     */
    private String host;

    /**
     * appid
     */
    private String appId;

    /**
     * app密钥
     */
    private String key;

}