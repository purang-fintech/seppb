package com.pr.sepp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class MultipartResolverConfig {

    //fixme 这些配置需要放到配置中心
    private static final String ENCODING = "utf-8";
    private static final Long maxUploadSize = 10485760000L;
    private static final Integer maxInMemorySize = 40960;


    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding(ENCODING);
        commonsMultipartResolver.setMaxUploadSize(maxUploadSize);
        commonsMultipartResolver.setMaxInMemorySize(maxInMemorySize);
        return commonsMultipartResolver;
    }
}
