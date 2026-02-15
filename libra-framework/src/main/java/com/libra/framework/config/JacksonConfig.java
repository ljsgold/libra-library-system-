package com.libra.framework.config;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;

@Configuration
@SuppressWarnings("all")
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            @SuppressWarnings("null")
            JsonSerializer<?> longSerializer = ToStringSerializer.instance;
            @SuppressWarnings("null")
            JsonSerializer<?> bigIntegerSerializer = ToStringSerializer.instance;
            @SuppressWarnings("null")
            Class<Long> longClass = Long.class;
            builder.serializerByType(longClass, longSerializer);
            builder.serializerByType(Long.TYPE, longSerializer);
            builder.serializerByType(BigInteger.class, bigIntegerSerializer);
        };
    }
}

