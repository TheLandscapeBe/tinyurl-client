package com.github.tinyurl.client.loadbalancer;

import com.github.tinyurl.client.constant.ErrorCode;
import com.github.tinyurl.client.exception.TinyUrlException;
import com.github.tinyurl.client.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡规则
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/16
 */
@Slf4j
public class RoundRobinRule implements Rule {

    private final LoadBalancer loadBalancer;

    private final AtomicInteger nextServerCounter = new AtomicInteger(0);

    public RoundRobinRule(final LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public Server choose(Object param) {
        List<Server> serverList = loadBalancer.getServers();
        if (ObjectUtil.isNull(serverList)) {
            throw new TinyUrlException(ErrorCode.CLIENT_HOST_NOT_EXISTING);
        }

       int nextServerIdx = incrementAndGetModulo(serverList.size());
        Server server = serverList.get(nextServerIdx);
        if (ObjectUtil.isNull(server)) {
            Thread.yield();
        }

        return server;
    }

    private int incrementAndGetModulo(int modulo) {
        int current;
        int next;
        do {
            current = this.nextServerCounter.get();
            next = (current + 1) % modulo;
        } while(!this.nextServerCounter.compareAndSet(current, next));

        return next;
    }

    @Override
    public void setLoadBalancer(LoadBalancer loadBalancer) {

    }

    @Override
    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }
}
