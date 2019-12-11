package com.pr.sepp.common.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import sun.reflect.misc.MethodUtil;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ScheduleMethodHandlerMapping
        extends RequestMappingInfoHandlerMapping
        implements HandlerMapping {
    /**
     * 包含@Scheduled注解方法的bean为handler
     * @param beanType
     * @return
     */
    @Override
    protected boolean isHandler(Class<?> beanType) {
        return Arrays.asList(MethodUtil.getMethods(beanType)).stream()
                    .anyMatch(method -> method.getAnnotation(Scheduled.class) != null);
    }

    /**
     * 处理@Scheduled标注方法，生成对于的RequestMappingInfo
     * @param method
     * @param handlerType
     * @return
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        if (method.getAnnotation(Scheduled.class) == null){
            return null;
        }
        return RequestMappingInfo.paths("/schedule/" + handlerType.getSimpleName() + "/" + method.getName())
                .methods(RequestMethod.GET)
                .build();
    }

    /**
     * 提高该HandlerMapping在HandlerMapping中的顺序，使其能优先处理
     * @return
     */
    @Override
    public int getOrder() {
        return 1;
    }
}