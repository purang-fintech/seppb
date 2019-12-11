package com.pr.sepp.common.constants;

import java.util.Properties;

import static org.apache.commons.lang.StringUtils.isBlank;

public enum  Env {
    LOCAL,
    DEV,
    TEST,
    PROD,
    UNKNOWN;

    public static Env getCurrentEnv() {
        Properties properties = System.getProperties();
        String env = properties.getProperty("env");
        if (isBlank(env)) {
            return UNKNOWN;
        }
        return Env.valueOf(env.toUpperCase());
    }
}
