package com.pr.sepp.utils.fasfdfs;

import com.pr.sepp.utils.fasfdfs.config.FdfsClientConfig;
import com.pr.sepp.utils.fasfdfs.config.FdfsConfig2Properties;
import com.pr.sepp.utils.fasfdfs.conn.ConnectionPoolConfig;
import com.pr.sepp.utils.fasfdfs.conn.FdfsConnectionPool;
import com.pr.sepp.utils.fasfdfs.server.FastDFSClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.TrackerClient;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FdfsProperties.class)
@ConditionalOnClass(TrackerClient.class)
@Slf4j
public class FdfsAutoConfiguration {

    private final FdfsProperties properties;

    public FdfsAutoConfiguration(FdfsProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConfigurationProperties(prefix = "fastdfs.pool")
    public ConnectionPoolConfig connectionPoolConfig() {
        return new ConnectionPoolConfig();
    }

    @Bean
    public FdfsClientConfig fdfsClientConfig() {
        FdfsClientConfig clientConfig = new FdfsClientConfig();
        log.info("Fastdfs properties: {}", properties.toString());
        BeanUtils.copyProperties(properties, clientConfig);
        if (StringUtils.isNotEmpty(properties.getTrackerServers())) {
            clientConfig.setTrackerServers(properties.getTrackerServers());
        } else {
            clientConfig.setTrackerServers("127.0.0.1:22122");
        }
        log.info("Fastdfs client config: {}", clientConfig.toString());
        return clientConfig;
    }

    @Bean
    public FdfsConfig2Properties fdfsConfig2Properties() {
        return new FdfsConfig2Properties(fdfsClientConfig());
    }

    @Bean
    public FdfsConnectionPool fdfsConnectionPool() {
        return new FdfsConnectionPool(fdfsConfig2Properties());
    }

    @Bean
    public FastDFSClient fastDFSClient(FdfsConnectionPool pool) {
        return new FastDFSClient(pool);
    }
}
