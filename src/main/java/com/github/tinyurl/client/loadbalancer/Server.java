package com.github.tinyurl.client.loadbalancer;

import com.github.tinyurl.client.constant.Constants;
import lombok.Data;

/**
 * 服务
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/16
 */
@Data
public class Server {

    private String host;

    private int port;

    private String scheme;

    private String url;

    public Server(String host, int port, String scheme) {
        this.host = host;
        this.port = port;

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(scheme).append(host);

        boolean nonHttp = port == 80 && !scheme.equals(Constants.HTTP_SCHEMA);
        boolean nonHttps = port == 443 && !scheme.equals(Constants.HTTPS_SCHEMA);
        if (nonHttp || nonHttps) {
            urlBuilder.append(this.port);
        }

        url = urlBuilder.toString();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUrl() {
        return url;
    }
}
