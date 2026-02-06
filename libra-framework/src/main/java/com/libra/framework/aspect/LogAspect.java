package com.libra.framework.aspect;

import cn.hutool.json.JSONUtil;
import com.libra.common.core.domain.SysLog;
import com.libra.framework.mapper.SysLogMapper;
import com.libra.common.core.annotation.Log;
import com.libra.common.utils.ServletUtils;
import com.libra.common.utils.TenantContext;
import com.libra.framework.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class LogAspect {
    private final SysLogMapper logMapper;

    // Explicit constructor injection to avoid Lombok issues in some environments
    public LogAspect(SysLogMapper logMapper) {
        this.logMapper = logMapper;
    }

    @Around("@annotation(auditLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, Log auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysLog sysLog = new SysLog();
        sysLog.setModule(auditLog.module());
        sysLog.setOperation(auditLog.operation());
        sysLog.setMethod(joinPoint.getSignature().getName());
        sysLog.setIp(ServletUtils.getIp());
        sysLog.setParams(JSONUtil.toJsonStr(joinPoint.getArgs()));
        sysLog.setTenantId(TenantContext.getTenantId());
        sysLog.setUserId(SecurityUtils.getUserId());
        sysLog.setUsername(SecurityUtils.getUsername());
        sysLog.setCreateTime(LocalDateTime.now());

        Object result;
        try {
            result = joinPoint.proceed();
            sysLog.setStatus(1);
            sysLog.setResult(JSONUtil.toJsonStr(result));
        } catch (Exception e) {
            sysLog.setStatus(0);
            sysLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            sysLog.setExecutionTime(System.currentTimeMillis() - startTime);
            logMapper.insert(sysLog);
        }
        return result;
    }
}
