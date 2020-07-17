package com.github.tinyurl.client.loadbalancer;

import java.util.List;

/**
 * 负载均衡接口
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/16
 */
public interface LoadBalancer {

    /**
     * 添加短链接服务
     * @param server 短链接服务
     */
    void addServer(Server server);

    /**
     * 选择短链接服务
     * @param param 可选参数
     * @return 服务器
     */
    Server chooseServer(Object param);

    /**
     * 获取服务列表
     * @return
     */
    List<Server> getServers();
}
