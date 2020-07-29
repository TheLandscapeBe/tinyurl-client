# 短连接服务客户端
短连接仓库地址：[短连接服务](https://github.com/TheLandscapeBe/tinyurl) <br>
https://github.com/TheLandscapeBe/tinyurl


#使用方法
1 maven依赖
```xml
<dependency>
    <groupId>com.github.fofcn.tinyurl</groupId>
    <artifactId>tinyurl-client</artifactId>
    <version>v2.0.2</version>
</dependency>
```
2 直接调用客户端
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
        tinyUrlParam.setDomain("s.xxx.com");
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

3 在spring boot中使用
<br>
3.1 配置RestTemplate
```java
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate createAndConfigRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(60000);
        requestFactory.setReadTimeout(6000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
}
```

3.2 配置TinyUrl客户端
```java

@Data
@Configuration
@ConfigurationProperties(prefix = "tinyurl")
public class TinyUrlClientConfigurer {

    /**
     * 域名,多个域名以逗号分割
     */
    private String host;

    /**
     * appid
     */
    private String appId;

    /**
     * app密钥
     */
    private String key;

    /**
     * 连接超时时间
     */
    private int connectTimeout = 60 * 1000;

    /**
     * 读取超时时间
     */
    private int readTimeout = 60 * 1000;

    /**
     * 最大连接数
     */
    private int maxTotal = 100;

    /**
     * 默认最大路由
     */
    private int defaultMaxRoute = 100;

    /**
     * 每个route最大连接数
     */
    private int maxPerRoute = 100;

    /**
     * 重试次数，默认为0
     */
    private int retryTimes = 0;

    /**
     * 链接资源释放周期，默认为1分钟
     */
    private int evictInterval = 60 * 1000;

    /**
     * 默认域名
     */
    private String domain;

    @Resource
    private RestTemplate restTemplate;


    @Bean
    public TinyUrlClient create() {
        TinyUrlClientConfig clientConfig = TinyUrlClientConfig.builder()
                .appId(appId)
                .key(key)
                .host(host)
                .restTemplate(restTemplate)
                .build();

        HttpPoolConfig httpPoolConfig = HttpPoolConfig.builder()
                .connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .maxTotal(maxTotal)
                .maxPerRoute(maxPerRoute)
                .defaultMaxRoute(defaultMaxRoute)
                .evictInterval(evictInterval)
                .retryTimes(0)
                .build();

        TinyUrlClient tinyUrlClient = new TinyUrlClientImpl(clientConfig, httpPoolConfig);
        tinyUrlClient.initialize();
        return tinyUrlClient;
    }
}


```


3.3 调用TinyUrlClient
```java
TinyUrlParam tinyUrlParam = new TinyUrlParam();
tinyUrlParam.setDomain(domain);
tinyUrlParam.setUrl(realUrl);
TinyUrlObject tinyUrlObject = tinyUrlClient.shorten(tinyUrlParam);
System.out.println(tinyUrlObject.getUrl())
```

