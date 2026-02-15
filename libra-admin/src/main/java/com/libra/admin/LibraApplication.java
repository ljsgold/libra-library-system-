package com.libra.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.libra.admin", "com.libra.framework"})
public class LibraApplication {
    public static void main(String[] args) {
        // 检查 Redis 配置（环境变量或配置文件）
        // 这样可以避免应用尝试连接 localhost:6379 导致启动失败
        String redisHost = System.getenv("SPRING_DATA_REDIS_HOST");
        
        // 如果环境变量未设置，使用 SpringApplicationBuilder 提前加载配置来检查
        if (redisHost == null || redisHost.trim().isEmpty()) {
            try {
                // 使用 SpringApplicationBuilder 提前初始化环境来读取配置文件
                // 设置 WebApplicationType.NONE 避免启动 Web 服务器
                SpringApplicationBuilder builder = new SpringApplicationBuilder(LibraApplication.class);
                builder.application().setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
                // 运行应用但立即关闭，只为了读取配置
                ConfigurableApplicationContext context = builder.run();
                redisHost = context.getEnvironment().getProperty("spring.data.redis.host", "");
                context.close();
            } catch (Exception e) {
                // 如果读取失败，继续使用空值
                redisHost = "";
            }
        }
        
        if (redisHost == null || redisHost.trim().isEmpty()) {
            System.setProperty("spring.autoconfigure.exclude", 
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration");
            System.out.println("Redis 未配置，已跳过 Redis 自动配置");
        } else {
            System.out.println("Redis 已配置，主机: " + redisHost);
        }
        SpringApplication.run(LibraApplication.class, args);
    }
}
