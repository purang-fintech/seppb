package com.pr.sepp.auth.core.config;

import com.pr.sepp.common.exception.SeppServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

@Component
public class AuthUrlHandlerMapping {


	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String PUT = "PUT";
	private static final String DELETE = "DELETE";

	@Autowired
    private ApplicationContext context;


	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Map<String, HandlerMapping> handlerMappingMap = context.getBeansOfType(HandlerMapping.class);
        Set<Map.Entry<String, HandlerMapping>> entries = handlerMappingMap.entrySet();
        for (Map.Entry<String, HandlerMapping> entry : entries) {
            HandlerExecutionChain handler = entry.getValue().getHandler(request);
            if (handler != null) {
                return handler;
            }
        }
		throw new SeppServerException(500, "访问的接口没有注册到容器");
	}

	public String mappingUrl(HttpServletRequest request) throws Exception {
		HandlerExecutionChain handler = getHandler(request);
		String[] value = null;
		//判断是否为注解@requestMapping()
		boolean isRequestMapping = ((HandlerMethod) handler.getHandler()).getMethod().isAnnotationPresent(RequestMapping.class);
		if (isRequestMapping) {
			value = ((HandlerMethod) handler.getHandler()).getMethod().getAnnotation(RequestMapping.class).value();
			return value[0];
		} else {
			if (GET.equalsIgnoreCase(request.getMethod())) {
				value = ((HandlerMethod) handler.getHandler()).getMethod().getAnnotation(GetMapping.class).value();
			}
			if (POST.equalsIgnoreCase(request.getMethod())) {
				value = ((HandlerMethod) handler.getHandler()).getMethod().getAnnotation(PostMapping.class).value();
			}
			if (DELETE.equalsIgnoreCase(request.getMethod())) {
				value = ((HandlerMethod) handler.getHandler()).getMethod().getAnnotation(DeleteMapping.class).value();
			}
			if (PUT.equalsIgnoreCase(request.getMethod())) {
				value = ((HandlerMethod) handler.getHandler()).getMethod().getAnnotation(PutMapping.class).value();
			}
			if (value != null && value.length != 0) {
				return value[0];
			}
		}
		return null;
	}

}
