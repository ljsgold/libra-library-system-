package com.libra.framework.aspect;

/*
import com.libra.common.core.annotation.RateLimiter;
import com.libra.common.exception.BusinessException;
import com.libra.common.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) {
        String key = rateLimiter.key() + ServletUtils.getRequestUri() + ":" + ServletUtils.getIp();
        int time = rateLimiter.time();
        int count = rateLimiter.count();

        Long current = redisTemplate.opsForValue().increment(key);
        if (current != null && current == 1) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }

        if (current != null && current > count) {
            throw new BusinessException(429, rateLimiter.message());
        }
    }
}
*/
