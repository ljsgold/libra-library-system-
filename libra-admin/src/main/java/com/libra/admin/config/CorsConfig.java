package com.libra.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有来源（生产环境建议配置具体域名）
        config.setAllowedOriginPatterns(List.of("*"));
        
        // 允许的请求头
        config.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization",
            "X-Requested-With",
            "Accept",
            "Origin"
        ));
        
        // 允许的请求方法
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // 注意：当使用通配符 "*" 时，不能设置 allowCredentials 为 true
        // 由于我们使用 JWT token（通过 Authorization header），不需要 cookies，所以设置为 false
        config.setAllowCredentials(false);
        
        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);
        
        // 允许暴露的响应头
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
