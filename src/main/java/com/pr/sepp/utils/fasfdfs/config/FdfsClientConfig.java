package com.pr.sepp.utils.fasfdfs.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.CharEncoding;

import static org.csource.fastdfs.ClientGlobal.DEFAULT_CONNECT_TIMEOUT;
import static org.csource.fastdfs.ClientGlobal.DEFAULT_NETWORK_TIMEOUT;

@Getter
@Setter
@ToString
public class FdfsClientConfig {
    private String charset = CharEncoding.UTF_8;
    private String httpAntiStealToken;
    private String httpSecretKey;
    private Integer httpTrackerHttpPort;
    private String trackerServers;
    private Integer connectTimeoutInSeconds = DEFAULT_CONNECT_TIMEOUT;
    private Integer networkTimeoutInSeconds = DEFAULT_NETWORK_TIMEOUT;
}
