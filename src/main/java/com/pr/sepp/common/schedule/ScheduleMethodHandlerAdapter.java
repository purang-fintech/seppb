package com.pr.sepp.common.schedule;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ScheduleMethodHandlerAdapter
        extends AbstractHandlerMethodAdapter
        implements HandlerAdapter {

    /**
     * 支持使用@Scheduled标准的handlerMethod对象
     * @param handlerMethod
     * @return
     */
    @Override
    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(Scheduled.class);
    }

    /**
     * 调用HandlerMethod方法，并返回json视图
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     */
    @Override
    protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {

        Map<String, Object> data = Maps.newHashMap();
        data.put("service", handlerMethod.getBeanType().getSimpleName());
        data.put("method", handlerMethod.getMethod().getName());
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            // 调用HandleMethod方法
            handlerMethod.getMethod().invoke(handlerMethod.getBean());
            stopwatch.stop();
            data.put("result", "success");
        }catch (Exception e){
            data.put("result", "error");
            data.put("exception", e.toString());
        }finally {
            data.put("cost", stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");

        }
        // 返回Json视图
        return new ModelAndView(new MappingJackson2JsonView(), data);
    }

    @Override
    protected long getLastModifiedInternal(HttpServletRequest request, HandlerMethod handlerMethod) {
        return 0;
    }

    /**
     * 提高在HandlerAdapter列表中的顺序，以优先处理
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}