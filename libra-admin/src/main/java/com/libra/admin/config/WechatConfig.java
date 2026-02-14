package com.libra.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatConfig {
    /**
     * 微信 AppID
     */
    private String appid;

    /**
     * 微信 AppSecret
     */
    private String secret;

    /**
     * 微信回调地址
     */
    private String redirectUri;
}
