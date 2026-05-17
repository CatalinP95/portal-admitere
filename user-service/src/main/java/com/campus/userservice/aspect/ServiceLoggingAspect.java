package com.campus.userservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Pointcut("execution(* com.campus.userservice.service.*.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logExecution(ProceedingJoinPoint pjp) throws Throwable {
        String sig = pjp.getSignature().toShortString();
        long start = System.currentTimeMillis();
        log.info(">> START {}", sig);
        try {
            Object result = pjp.proceed();
            log.info("<< END {} in {}ms", sig, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("!! FAIL {} after {}ms: {}", sig, System.currentTimeMillis() - start, e.getMessage());
            throw e;
        }
    }
}
