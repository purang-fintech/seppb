package com.pr.sepp.utils.jenkins;

import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.mgr.system.model.SystemSetting;
import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.pr.sepp.utils.jenkins.model.SettingType.JENKINS;

@Slf4j
public class DynamicJenkinsClientProvider extends JenkinsClientProvider {

    public DynamicJenkinsClientProvider(JenkinsProperties jenkinsProperties,
                                        SettingDAO settingDAO) {
        super(jenkinsProperties, settingDAO);
    }

    @Override
    protected boolean shouldUpdateJenkinsClient() {
        return true;
    }

    @Override
    protected void retrieveJenkinsClient() {
        SystemSetting setting = settingDAO.findSetting(JENKINS);
        try {
            List<SystemSetting.JenkinsConfig> jenkinsConfigs = SystemSetting.settingToJenkinsConfig(setting);
            for (SystemSetting.JenkinsConfig jenkinsConfig : jenkinsConfigs) {
                JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(URI.create(jenkinsConfig.getBuilderServer()),
                        jenkinsConfig.getBuilderUser(), jenkinsConfig.getPassword());
                JenkinsServer jenkinsServer = new JenkinsServer(jenkinsHttpClient);
                JenkinsClient jenkinsClient = new JenkinsClient(jenkinsServer, jenkinsHttpClient);
                jenkinsClientProviderMap.put(jenkinsConfig.getServerType().getBeanName(), jenkinsClient);
            }
        } catch (IOException e) {
            log.error("获取jenkins配置失败{}", e);
        }
    }
}
