package com.github.tinyurl.client.constant;

/**
 * 错误编码
 *
 * @author errorfatal89@gmail.com
 * @date 2020/07/07
 */
public enum ErrorCode {
    SUCCESS(0, "成功"),
    SYSTEM_ERROR(1, "系统错误"),
    DOMAIN_NOT_EXISTS(100, "指定的域名不存在"),
    RECORD_NOT_EXISTS(101, "记录不存在"),
    TS_EXPIRED(102, "请求时间过长"),
    APP_NOT_EXISTING(103, "应用程序未申请"),
    NONCE_STR_TOO_SHORT(103, "nonceStr长度太短,至少16位"),
    SIGN_NOT_EXISTING(104, "请进行签名（miss sign field）"),
    SIGN_NOT_MATCH(105, "签名错误（sign error）"),
    UID_GEN_TYPE_NOT_EXISTING(106, "UID生成算法不存在（uid generator does not exists）"),
    CLIENT_HOST_NOT_CONFIGURE(1000, "尚未配置HOST（Host not configure）"),
    CLIENT_HOST_NOT_EXISTING(1001, "服务不存在（Host not existing）"),
    CLIENT_HOST_SCHEMA_ERROR(1002, "服务应该以http或https开头（host should start with http/https）"),
    CLIENT_HOST_FORMAT_ERROR(1003, "服务格式配置不正确（host format error）"),
    CLIENT_REMOTE_CALL_ERROR(1004, "远程调用失败(client remote call error)"),
    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
