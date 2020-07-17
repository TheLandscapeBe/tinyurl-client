package com.github.tinyurl.client.loadbalancer;


/**
 * 负载均衡规则
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/16
 */
public interface Rule {
    /**
     * 选择服务
     * @param var1
     * @return
     */
    Server choose(Object var1);

    /**
     * 设置负载均衡
     * @param var1
     */
    void setLoadBalancer(LoadBalancer var1);

    /**
     * 获取负载均衡器
     * @return
     */
    LoadBalancer getLoadBalancer();
}
