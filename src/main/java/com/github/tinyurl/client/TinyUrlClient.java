package com.github.tinyurl.client;

import com.github.tinyurl.client.entity.TinyUrlObject;
import com.github.tinyurl.client.entity.TinyUrlParam;

/**
 * tiny url客户端接口定义
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/16
 */
public interface TinyUrlClient {

    /**
     * 初始化
     */
    void initialize();

    /**
     * 生成短连接
     * @param tinyUrlParam 生成短连接参数
     * @return 短连接对象
     */
    TinyUrlObject shorten(TinyUrlParam tinyUrlParam);
}
