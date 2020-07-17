package com.github.tinyurl.client.util;



import com.github.tinyurl.client.constant.Constants;

import java.util.Map;

/**
 * 签名工具类
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/07
 */
public class SignUtil {

    private SignUtil() {}

    public static final String SIGN_KEY = "sign";

    public static final String ACCESS_KEY = "key";

    /**
     * 签名参数列表
     * @param params 参数表
     * @param key 参数密钥
     * @return 签名字符串
     */
    public static String sign(Map<String, String> params, String key) {
        StringBuilder paramString = new StringBuilder();
        params.forEach((paramKey, value) -> paramString.append(paramKey).append(Constants.EQUALS_MARK)
                .append(value).append(Constants.AMPERSAND));
        paramString.append(ACCESS_KEY).append(Constants.EQUALS_MARK).append(key);

        return HmacUtil.hmacSha256(paramString.toString(), key);
    }
}
