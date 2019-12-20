package com.pr.sepp.utils.jenkins.pool;

import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JenkinsPoolConfig extends GenericObjectPoolConfig {


    public JenkinsPoolConfig(JenkinsProperties jenkinsProperties) {
        setTestWhileIdle(jenkinsProperties.isTestWhileIdle());
        setMinEvictableIdleTimeMillis(jenkinsProperties.getMinEvictableIdleTimeMillis());
        setTimeBetweenEvictionRunsMillis(jenkinsProperties.getTimeBetweenEvictionRunsMillis());
        setEvictorShutdownTimeoutMillis(jenkinsProperties.getEvictorShutdownTimeoutMillis());
        setNumTestsPerEvictionRun(jenkinsProperties.getNumTestsPerEvictionRun());
        setTestOnBorrow(jenkinsProperties.isTestOnBorrow());
        setTestOnReturn(jenkinsProperties.isTestOnReturn());
        //最大连接数
        setMaxTotal(jenkinsProperties.getMaxTotal());
        setMaxIdle(jenkinsProperties.getMaxIdle());
        setMaxWaitMillis(jenkinsProperties.getMaxWaitMillis());
    }


    public JenkinsPoolConfig() {
        setTestWhileIdle(true);
        setMinEvictableIdleTimeMillis(240_000);
        setTimeBetweenEvictionRunsMillis(30_000);
        setEvictorShutdownTimeoutMillis(5_000);
        setSoftMinEvictableIdleTimeMillis(5_000);
        setNumTestsPerEvictionRun(-1);
        setTestOnBorrow(true);
        setTestOnReturn(true);
        //最大连接数
        setMaxTotal(6);
        setMaxIdle(6);
        setMaxWaitMillis(4_000);
    }
}
