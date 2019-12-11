package com.pr.sepp.utils.fasfdfs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@ConfigurationProperties(prefix = "fastdfs")
@Setter
@Getter
@ToString
public class FdfsProperties {
    private String connectTimeoutInSeconds;
    private String networkTimeoutInSeconds;
    private String charset;
    private String httpAntiStealToken;
    private String httpSecretKey;
    private String httpTrackerHttpPort;
    private String trackerServers;
    private String server;
    private Properties properties = new Properties();
    private String groupName;

    public void setConnectTimeoutInSeconds(String connectTimeoutInSeconds) {
        this.connectTimeoutInSeconds = connectTimeoutInSeconds;
        properties.put("fastdfs.connectTimeoutInSeconds",connectTimeoutInSeconds);
    }

    public void setNetworkTimeoutInSeconds(String networkTimeoutInSeconds) {
        this.networkTimeoutInSeconds = networkTimeoutInSeconds;
        properties.put("fastdfs.networkTimeoutInSeconds",networkTimeoutInSeconds);
    }

    public void setCharset(String charset) {
        this.charset = charset;
        properties.put("fastdfs.charset",charset);
    }

    public void setHttpAntiStealToken(String httpAntiStealToken) {
        this.httpAntiStealToken = httpAntiStealToken;
        properties.put("fastdfs.httpAntiStealToken",httpAntiStealToken);
    }

    public void setHttpSecretKey(String httpSecretKey) {
        this.httpSecretKey = httpSecretKey;
        properties.put("fastdfs.httpSecretKey",httpSecretKey);
    }

    public void setHttpTrackerHttpPort(String httpTrackerHttpPort) {
        this.httpTrackerHttpPort = httpTrackerHttpPort;
        properties.put("fastdfs.httpTrackerHttpPort",httpTrackerHttpPort);
    }

    public void setTrackerServers(String trackerServers) {
        this.trackerServers = trackerServers;
        properties.put("fastdfs.trackerServers",trackerServers);
    }

    public void setServer(String server) {
        this.server = server;
        properties.put("fastdfs.server",server);
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
        properties.put("fastdfs.groupName",groupName);
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
