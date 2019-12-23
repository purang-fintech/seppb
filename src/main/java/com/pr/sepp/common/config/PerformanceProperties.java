package com.pr.sepp.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;
import java.util.Properties;

import static com.google.common.base.Verify.verifyNotNull;
import static java.lang.String.valueOf;

@ConfigurationProperties(prefix = PerformanceProperties.PERFORMANCE_PREFIX)
public class PerformanceProperties {

    public static final String PERFORMANCE_PREFIX = "performance";
    private static final String SLOW_TIME = "slowTime";

    private Long slowTime;

    private Properties properties = new Properties();

    public Properties getProperties() {
        if (Objects.isNull(properties.getProperty(SLOW_TIME))) {
            properties.setProperty(SLOW_TIME, valueOf(500));
        }
        return properties;
    }

    public Long getSlowTime() {
        return slowTime;
    }

    public void setSlowTime(Long slowTime) {
        this.slowTime = slowTime;
        properties.setProperty(SLOW_TIME, verifyNotNull(valueOf(slowTime)));
    }
}
