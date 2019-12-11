package com.pr.sepp.common.config.factory;

import com.pr.sepp.common.constants.Env;
import com.pr.sepp.utils.AESEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import static com.pr.sepp.common.constants.Env.LOCAL;
import static com.google.common.base.Strings.nullToEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Slf4j
public class DefinitionPropertySourceFactory implements PropertySourceFactory, EnvironmentPostProcessor, Ordered {

    //fixme
    private static String CONFIG_PATH = "D://projects/spring.properties";
    private static final String aesKey = "0807060504030201";
    private static final String JDBC_USER_NAME = "jdbc.username";
    private static final String JDBC_PASSWORD = "jdbc.password";
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private static final String SEPP_LOG_PATH = "seppLogPath";

    /**
     * 通过{@link org.springframework.context.annotation.PropertySource} 设置参数factory执行方法
     *
     * @param name
     * @param resource
     * @return
     * @throws IOException
     */
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (Env.getCurrentEnv() != LOCAL) {
            Properties properties = new Properties();
            return new PropertiesPropertySource(UUID.randomUUID().toString(), properties);
        }
        Properties properties = buildDecryptProperties(resource.getInputStream());
        return new PropertiesPropertySource(nullToEmpty(name), properties);
    }

    /**
     * 从外部获取配置文件,并对加密后的数据库用户名和密码做解密
     *
     * @param environment
     * @param application
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String configPath = environment.getProperty("path");
        if (Env.getCurrentEnv() == LOCAL) {
            eagerLoad(environment);
            return;
        }
        if (isNotBlank(configPath)) {
            CONFIG_PATH = configPath;
        }
        File file = new File(CONFIG_PATH);
        log.info("configPath:{}", CONFIG_PATH);
        try (InputStream input = new FileInputStream(file)) {
            Properties properties = buildDecryptProperties(input);
            PropertiesPropertySource propertySource = new PropertiesPropertySource("spring", properties);
            environment.getPropertySources().addLast(propertySource);
        } catch (Exception e) {
            log.error("配置文件读取错误", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 需要在日志加载之前，将logpath加载到环境变量
     *
     * @param environment
     */
    private void eagerLoad(ConfigurableEnvironment environment) {
        try {
            initialize(environment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties buildDecryptProperties(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.setProperty(JDBC_USER_NAME, convertProperty(properties.getProperty(JDBC_USER_NAME)));
        properties.setProperty(JDBC_PASSWORD, convertProperty(properties.getProperty(JDBC_PASSWORD)));
        return properties;
    }

    protected void initialize(ConfigurableEnvironment environment) throws IOException {

        String resolvedLocation = environment.resolveRequiredPlaceholders("classpath:spring.properties");
        Resource resource = this.resourceLoader.getResource(resolvedLocation);
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        String logPath = properties.getProperty(SEPP_LOG_PATH);
        if (StringUtils.isNotBlank(logPath)) {
            Properties logProperties = new Properties();
            logProperties.setProperty(SEPP_LOG_PATH, logPath);
            PropertiesPropertySource propertySource = new PropertiesPropertySource(UUID.randomUUID().toString(), logProperties);
            environment.getPropertySources().addLast(propertySource);
        }
    }

    private String convertProperty(String value) {
        return AESEncryptor.decrypt(nullToEmpty(value), aesKey);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
