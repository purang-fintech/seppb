package com.pr.sepp.auth.core.permission.aspect;

import com.pr.sepp.auth.core.config.AuthUrlHandlerMapping;
import com.pr.sepp.auth.core.permission.annotation.ApiPermission;
import com.pr.sepp.auth.model.Privilege;
import com.pr.sepp.auth.model.ResourceConfig;
import com.pr.sepp.auth.service.ResourceConfigService;
import com.pr.sepp.auth.service.UserRoleService;
import com.pr.sepp.common.exception.SeppClientException;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Component
public class ApiPermissionInterceptor extends HandlerInterceptorAdapter {

    private static final String NO_ROLE_ERROR_MESSAGE = "您没有权限进行此操作，请查看您的角色";
    @Autowired
    private ResourceConfigService resourceConfigService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private AuthUrlHandlerMapping authUrlHandlerMapping;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            loginKeepLive(request,response);
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            ApiPermission apiPermission = handlerMethod.getMethodAnnotation(ApiPermission.class);
            if (!isNull(apiPermission)) {
                List<Privilege> currentUserRoles = userRoleService.getPrivilegesByUserAndProductId(ParameterThreadLocal.getUserId(), ParameterThreadLocal.getProductId());
                //判断是否需要动态获取当前用户的角色，以及当前访问接口配置的角色
                if (apiPermission.dynamic()) {
                    //获取当前接口的访问角色
                    List<ResourceConfig> resourceConfigs = resourceConfigService.listResourceConfigsByApi(authUrlHandlerMapping.mappingUrl(request), request.getMethod());
                    //获取该用户的角色
                    hasAccessRoles(resourceConfigs, currentUserRoles);
                }
            }
        }
        return super.preHandle(request, response, handler);
    }

    private void hasAccessRoles(List<ResourceConfig> resourceConfigs, List<Privilege> currentUserRoles) {
        if (CollectionUtils.isNotEmpty(resourceConfigs)) {
            List<Integer> userRoleIds = resourceConfigs.stream().map(ResourceConfig::getRoleId).collect(toList());
            List<Integer> currentUserRoleIds = currentUserRoles.stream().map(Privilege::getRoleId).collect(toList());
            userRoleIds.retainAll(currentUserRoleIds);
            if (Objects.equals(userRoleIds.size(), 0)) {
                throw new SeppClientException(NO_ROLE_ERROR_MESSAGE);
            }
        }
    }

    private void loginKeepLive(HttpServletRequest request,HttpServletResponse response) {
        if ("/sepp/sepp".equalsIgnoreCase(request.getRequestURI())) {
            userRoleService.checkLoginAuth(request,response);
        }
    }
}
