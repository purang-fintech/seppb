package com.pr.sepp.common.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerExecuteIntervalAspect extends CommonAspect {

    @Override
    @Around("execution(public * com.pr.sepp.*.controller.*.*(..)) || execution(public * com.pr.sepp.*.*.controller.*.*(..))")
    public Object printExecuteIntervalLog(ProceedingJoinPoint point) throws Throwable {
        return super.printExecuteIntervalLog(point);
    }
}
