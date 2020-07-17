# 短连接服务客户端
短连接仓库地址：[短连接服务](https://github.com/fofcn/tinyurl) <br>
https://github.com/fofcn/tinyurl


#使用方法
1 maven依赖
```xml

```
2 调用客户端
```java
public class TinyUrlClientTest {

    private static TinyUrlClientConfig clientConfig;

    private static HttpPoolConfig httpPoolConfig;

    private static TinyUrlClient tinyUrlClient;

    @BeforeClass
    public static void beforeClass() {
        clientConfig = TinyUrlClientConfig.builder()
                .appId("1594708959736")
                .key("1594708959736")
                .host("http://localhost:80")
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
        TinyUrlParam tinyUrlParam = new TinyUrlParam();
        tinyUrlParam.setDomain("s.pudedu.com");
        tinyUrlParam.setUrl("https://mvnrepository.com/artifact/org.slf4j/slf4j-api/1.7.30");
        TinyUrlObject tinyUrlObject = tinyUrlClient.shorten(tinyUrlParam);
        if (ObjectUtil.isNull(tinyUrlObject)) {
            Assert.fail();
        }

        Assert.assertNotNull(tinyUrlObject.getUrl());
        System.out.println(tinyUrlObject.getUrl());
    }
}
```


