package com.github.tinyurl.client;

import com.github.tinyurl.client.config.HttpPoolConfig;
import com.github.tinyurl.client.config.TinyUrlClientConfig;
import com.github.tinyurl.client.entity.TinyUrlObject;
import com.github.tinyurl.client.entity.TinyUrlParam;
import com.github.tinyurl.client.util.ObjectUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TinyUrl测试类
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/17
 */
public class TinyUrlClientTest {

    private static TinyUrlClientConfig clientConfig;

    private static HttpPoolConfig httpPoolConfig;

    private static TinyUrlClient tinyUrlClient;

    @BeforeClass
    public static void beforeClass() {
        clientConfig = TinyUrlClientConfig.builder()
                .appId("1594708959736")
                .key("1594708959736")
                .host("http://localhost:80;http://192.168.2.175:53000")
                .build();

        httpPoolConfig = HttpPoolConfig.builder()
                .connectTimeout(60000)
                .readTimeout(6000)
                .maxTotal(100)
                .maxPerRoute(100)
                .defaultMaxRoute(100)
                .build();

        tinyUrlClient = new TinyUrlClientImpl(clientConfig, httpPoolConfig);
        tinyUrlClient.initialize();
    }

    @Test
    public void testShorten() {
        for (int i = 0; i < 100; i++) {
            TinyUrlParam tinyUrlParam = new TinyUrlParam();
            tinyUrlParam.setDomain("s.url.com");
            tinyUrlParam.setUrl("https://mvnrepository.com/artifact/org.slf4j/slf4j-api/1.7.30" + i);
            TinyUrlObject tinyUrlObject = tinyUrlClient.shorten(tinyUrlParam);
            if (ObjectUtil.isNull(tinyUrlObject)) {
                Assert.fail();
            }

            Assert.assertNotNull(tinyUrlObject.getUrl());
            System.out.println(tinyUrlObject.getUrl());
        }

    }
}
