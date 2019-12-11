package com.pr.sepp.auth.core.config;

import com.pr.sepp.auth.core.permission.aspect.ApiPermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthWebMvcConfigurerAdapter implements WebMvcConfigurer {

    @Autowired
    private ApiPermissionInterceptor apiPermissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiPermissionInterceptor);
    }
}
