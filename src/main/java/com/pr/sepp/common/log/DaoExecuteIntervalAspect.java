package com.pr.sepp.common.log;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DaoExecuteIntervalAspect extends CommonAspect {

    @Override
    @Around("execution(public * com.pr.sepp.*.dao.*.*(..))|| execution(public * com.pr.sepp.*.*.dao.*.*(..))")
    public Object printExecuteIntervalLog(ProceedingJoinPoint point) throws Throwable {
        return super.printExecuteIntervalLog(point);
    }

}
