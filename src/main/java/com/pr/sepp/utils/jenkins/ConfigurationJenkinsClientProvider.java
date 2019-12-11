package com.pr.sepp.utils.jenkins;

import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;

import java.net.URI;

/**
 * 该类仅在本地开发时会读取配置文件中的jenkins的信息（可以讲jenkins.enable-programmatic配置置为true）
 */
public class ConfigurationJenkinsClientProvider extends JenkinsClientProvider {

    public ConfigurationJenkinsClientProvider(JenkinsProperties jenkinsProperties,
                                              SettingDAO settingDAO) {
        super(jenkinsProperties, settingDAO);
    }

    @Override
    protected boolean shouldUpdateJenkinsClient() {
        return false;
    }

    @Override
    protected void retrieveJenkinsClient() {
        if (jenkinsProperties.androidJenkinsNonNull()) {
            JenkinsHttpClient androidJenkinsHttpClient = new JenkinsHttpClient(URI.create(jenkinsProperties.getAndroidUrl()),
                    jenkinsProperties.getAndroidUsername(), jenkinsProperties.getAndroidPassword());
            JenkinsServer androidJenkinsServer = new JenkinsServer(androidJenkinsHttpClient);
            JenkinsClient androidJenkinsClient = new JenkinsClient(androidJenkinsServer, androidJenkinsHttpClient);
            jenkinsClientProviderMap.put(InstanceType.ANDROID.getBeanName(), androidJenkinsClient);
        }

        if (jenkinsProperties.iosJenkinsNonNull()) {
            JenkinsHttpClient iosJenkinsHttpClient = new JenkinsHttpClient(URI.create(jenkinsProperties.getIosUrl()),
                    jenkinsProperties.getIosUsername(), jenkinsProperties.getIosPassword());
            JenkinsServer iosJenkinsServer = new JenkinsServer(iosJenkinsHttpClient);
            JenkinsClient iosJenkinsClient = new JenkinsClient(iosJenkinsServer, iosJenkinsHttpClient);
            jenkinsClientProviderMap.put(InstanceType.IOS.getBeanName(), iosJenkinsClient);
        }

        if (jenkinsProperties.webJenkinsNonNull()) {
            JenkinsHttpClient webJenkinsHttpClient = new JenkinsHttpClient(URI.create(jenkinsProperties.getWebUrl()),
                    jenkinsProperties.getWebUsername(), jenkinsProperties.getWebPassword());
            JenkinsServer webJenkinsServer = new JenkinsServer(webJenkinsHttpClient);
            JenkinsClient webJenkinsClient = new JenkinsClient(webJenkinsServer, webJenkinsHttpClient);
            jenkinsClientProviderMap.put(InstanceType.ORDINARY.getBeanName(), webJenkinsClient);
        }
    }
}
