package com.pr.sepp.common.filter;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@WebFilter(urlPatterns = "/*", filterName = "parameterFilter")
public class ParameterFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String userId = request.getParameter("userId");
        String productId = request.getParameter("productId");
        String pageNum = request.getParameter("pageNum");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(userId)) {
            ParameterThreadLocal.setUserId(Integer.valueOf(userId));
        }
        if (StringUtils.isNotBlank(productId)) {
            ParameterThreadLocal.setProductId(Integer.valueOf(productId));
        }
        if (StringUtils.isNotBlank(pageNum)) {
            ParameterThreadLocal.setPageNum(Integer.valueOf(pageNum));
        } else {
            ParameterThreadLocal.setPageNum(1);
        }
        if (StringUtils.isNotBlank(pageSize)) {
            ParameterThreadLocal.setPageSize(Integer.valueOf(pageSize));
        } else {
            ParameterThreadLocal.setPageSize(500);
        }
        ParameterThreadLocal.setHttpId(UUID.randomUUID().toString());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
