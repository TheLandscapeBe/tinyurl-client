package com.github.tinyurl.client.loadbalancer;


import java.util.ArrayList;
import java.util.List;

/**
 * 基础负载均衡
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/16
 */
public class BaseLoadBalancer implements LoadBalancer {
    private Rule rule = new RoundRobinRule(this);
    private final List<Server> serverList = new ArrayList<>();

    public BaseLoadBalancer() {
    }

    @Override
    public void addServer(Server server) {
        serverList.add(server);
    }

    @Override
    public Server chooseServer(Object param) {
        return rule.choose(param);
    }

    @Override
    public List<Server> getServers() {
        return serverList;
    }
}
