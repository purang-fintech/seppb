package com.pr.sepp.utils.jenkins;

import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class JenkinsClientProvider {

    protected JenkinsProperties jenkinsProperties;
    protected SettingDAO settingDAO;
    protected Map<String, JenkinsClient> jenkinsClientProviderMap = new ConcurrentHashMap<>();

    JenkinsClientProvider(JenkinsProperties jenkinsProperties,
                          SettingDAO settingDAO) {
        this.jenkinsProperties = jenkinsProperties;
        this.settingDAO = settingDAO;
    }

    public JenkinsClient getJenkinsClient(InstanceType instanceType) {
        log.debug("获取jenkinsClient");
        return jenkinsClientProviderMap.get(instanceType.getBeanName());
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
