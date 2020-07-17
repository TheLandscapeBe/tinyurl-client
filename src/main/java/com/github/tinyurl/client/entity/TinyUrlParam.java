package com.github.tinyurl.client.entity;

import lombok.Data;

/**
 * 短连接请求
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/15
 */
@Data
public class TinyUrlParam {

    private String url;

    private String domain;

}
