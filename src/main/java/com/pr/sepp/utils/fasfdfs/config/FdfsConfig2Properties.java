package com.pr.sepp.utils.fasfdfs.config;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

public class FdfsConfig2Properties {
    private static String propPrefix = "fastdfs.";
    private static final char UNDERLINE = '_';

    private FdfsClientConfig clientConfig;

    public FdfsConfig2Properties(FdfsClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public Properties initByFdfsClientConfig() {
        Properties prop = new Properties();
        Class<?> clazz = clientConfig.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get")) {
                name = name.substring("get".length());
                if (StringUtils.isBlank(name)) {
                    continue;
                }
                name = StringUtils.lowerCase(camelToUnderline(name));
                if (name.startsWith("fastdfs")) {
                    name = name.substring("fastdfs".length());
                }
                if (name.startsWith("_")) {
                    name = name.substring(1);
                }
                if (StringUtils.equalsIgnoreCase("class", name)) {
                    continue;
                }
                name = propPrefix + name;
                try {
                    Object result = method.invoke(clientConfig);
                    if (result != null) {
                        if (result instanceof String) {
                            if (StringUtils.isNotBlank((CharSequence) result)) {
                                prop.put(name, result);
                            }
                        } else {
                            prop.put(name, result);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return prop;
    }

    private String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
