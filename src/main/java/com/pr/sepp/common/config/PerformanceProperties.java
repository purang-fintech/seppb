package com.pr.sepp.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;
import java.util.Properties;

import static com.google.common.base.Verify.verifyNotNull;
import static java.lang.String.valueOf;

@ConfigurationProperties(prefix = PerformanceProperties.PERFORMANCE_PREFIX)
public class PerformanceProperties {

    public static final String PERFORMANCE_PREFIX = "performance";

    private Long slowTime;

    private Properties properties = new Properties();

    public Properties getProperties() {
        if (Objects.isNull(properties.getProperty("slowTime"))) {
            properties.setProperty("slowTime", valueOf(500));
        }
        return properties;
    }

    public Long getSlowTime() {
        return slowTime;
    }

    public void setSlowTime(Long slowTime) {
        this.slowTime = slowTime;
        properties.setProperty("slowTime", verifyNotNull(valueOf(slowTime)));
    }
}
