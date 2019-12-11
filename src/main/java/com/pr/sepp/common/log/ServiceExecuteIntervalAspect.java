package com.pr.sepp.common.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceExecuteIntervalAspect extends CommonAspect {


    @Override
    @Around("execution(public * com.pr.sepp.*.service.*.*(..)) || execution(public * com.pr.sepp.*.*.service.*.*(..))")
    public Object printExecuteIntervalLog(ProceedingJoinPoint point) throws Throwable {
        return super.printExecuteIntervalLog(point);
    }
}