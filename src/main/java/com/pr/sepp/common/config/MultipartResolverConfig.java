package com.pr.sepp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class MultipartResolverConfig {

    private static final String ENCODING = "utf-8";
    private static final Long MAX_UPLOAD_SIZE = 10485760000L;
    private static final Integer MAX_IN_MEMORY_SIZE = 40960;


    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding(ENCODING);
        commonsMultipartResolver.setMaxUploadSize(MAX_UPLOAD_SIZE);
        commonsMultipartResolver.setMaxInMemorySize(MAX_IN_MEMORY_SIZE);
        return commonsMultipartResolver;
    }
}
