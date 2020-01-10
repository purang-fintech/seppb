package com.pr.sepp.utils.jenkins.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@ConfigurationProperties(prefix = "jenkins")
public class JenkinsProperties {

    private String webUsername;
    private String webPassword;
    private String webUrl;

    private String iosUsername;
    private String iosPassword;
    private String iosUrl;

    private String androidUsername;
    private String androidPassword;
    private String androidUrl;
    /**
     * 该默认参数为true时代表读取配置文件的jenkins，如果部署到生产环境请置为false
     */
    private boolean enableProgrammatic = true;
    /**
     * 该状态为false时则不会去同步jenkins状态、如果为true则表示同步实时同步jenkins状态
     */
    private boolean enableSyncStatus = true;

    //连接池相关配置
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = true;
    private boolean testOnReturn = true;
    private int minEvictableIdleTimeMillis = 240_000;
    private int timeBetweenEvictionRunsMillis = 30_000;
    private int evictorShutdownTimeoutMillis = 5_000;
    private int maxWaitMillis = 4_000;
    private int numTestsPerEvictionRun = -1;
    private int maxTotal = 6;
    private int maxIdle = 6;


    public boolean webJenkinsNonNull() {
        return isNotBlank(webUrl) && isNotBlank(webUsername) && isNotBlank(webPassword);
    }

    public boolean iosJenkinsNonNull() {
        return isNotBlank(iosUrl) && isNotBlank(iosUsername) && isNotBlank(iosPassword);
    }

    public boolean androidJenkinsNonNull() {
        return isNotBlank(androidUrl) && isNotBlank(androidUsername) && isNotBlank(androidPassword);
    }

}
