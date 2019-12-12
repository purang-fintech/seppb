package com.pr.sepp.utils.jenkins;

import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.mgr.system.model.SystemSetting;
import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class DynamicJenkinsClientProvider extends JenkinsClientProvider {

    public DynamicJenkinsClientProvider(JenkinsProperties jenkinsProperties, SettingDAO settingDAO) {
        super(jenkinsProperties, settingDAO);
    }

    @Override
    protected boolean shouldUpdateJenkinsClient() {
        return true;
    }

    @Override
    protected void retrieveJenkinsClient() {
        SystemSetting setting = settingDAO.findSetting(3);
        try {
            List<SystemSetting.JenkinsConfig> jenkinsConfigs = SystemSetting.settingToJenkinsConfig(setting);
            for (SystemSetting.JenkinsConfig jenkinsConfig : jenkinsConfigs) {
                JenkinsClient jenkinsClient = createJenkinsClient(jenkinsConfig.getBuilderServer(), jenkinsConfig.getBuilderUser(),
                        jenkinsConfig.getPassword());
                JenkinsClient buildJenkinsClient = createJenkinsClient(jenkinsConfig.getBuilderServer(), jenkinsConfig.getBuilderUser(),
                        jenkinsConfig.getPassword());
                jenkinsClientProviderMap.put(jenkinsConfig.getServerType().getBeanName(), jenkinsClient);
                buildJenkinsClientProviderMap.put(jenkinsConfig.getServerType().getBeanName(), buildJenkinsClient);
            }
        } catch (IOException e) {
            log.error("获取jenkins配置失败{}", e);
        }
    }
}
