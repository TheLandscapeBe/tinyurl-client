package com.github.tinyurl.client.util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * url解析工具类
 *
 * @author jiquanxi
 * @date 2020/07/17
 */
public class UrlUtil {

    public static InetSocketAddress socketAddress(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }

        return new InetSocketAddress(hostname, port);
    }

}
