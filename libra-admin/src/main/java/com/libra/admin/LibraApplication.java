package com.libra.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.libra.admin", "com.libra.framework"})
public class LibraApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraApplication.class, args);
    }
}
