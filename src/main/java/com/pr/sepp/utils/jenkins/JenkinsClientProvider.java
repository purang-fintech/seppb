package com.pr.sepp.utils.jenkins;

import com.pr.sepp.common.exception.SeppClientException;
import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import com.pr.sepp.utils.jenkins.pool.JenkinsPool;
import com.pr.sepp.utils.jenkins.pool.JenkinsPoolConfig;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class JenkinsClientProvider {

    protected JenkinsProperties jenkinsProperties;
    protected SettingDAO settingDAO;
    protected Map<String, JenkinsPool> jenkinsClientProviderMap = new ConcurrentHashMap<>();

    JenkinsClientProvider(JenkinsProperties jenkinsProperties, SettingDAO settingDAO) {
        this.jenkinsProperties = jenkinsProperties;
        this.settingDAO = settingDAO;
    }

    public JenkinsClient getJenkinsClient(InstanceType instanceType) {
        log.debug("获取jenkinsClient");
        JenkinsPool jenkinsPool = jenkinsClientProviderMap.get(instanceType.getBeanName());
        if (jenkinsPool == null) {
            throw new SeppClientException(String.format("类型为:%s的连接池为空", instanceType.getBeanName()));
        }
        return jenkinsPool.getResource();
    }

    protected JenkinsPoolConfig createJenkinsPoolConfig() {
        return new JenkinsPoolConfig(jenkinsProperties);
    }


    @PostConstruct
    protected synchronized void updateJenkinsClient() {
        retrieveJenkinsClient();
    }

    public void dynamicUpdateJenkinsClient() {
        if (shouldUpdateJenkinsClient()) {
            retrieveJenkinsClient();
        }
    }


    protected abstract boolean shouldUpdateJenkinsClient();

    protected abstract void retrieveJenkinsClient();

}
