package com.pr.sepp.utils.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class JenkinsClientProvider {

    protected JenkinsProperties jenkinsProperties;
    protected SettingDAO settingDAO;
    protected Map<String, JenkinsClient> jenkinsClientProviderMap = new ConcurrentHashMap<>();
    protected Map<String, JenkinsClient> buildJenkinsClientProviderMap = new ConcurrentHashMap<>();
    protected HttpClientBuilder clientBuilder;

    JenkinsClientProvider(JenkinsProperties jenkinsProperties, SettingDAO settingDAO) {
        this.jenkinsProperties = jenkinsProperties;
        this.settingDAO = settingDAO;
        this.clientBuilder = clientBuilder();
    }

    public JenkinsClient getJenkinsClient(InstanceType instanceType) {
        log.debug("获取jenkinsClient");
        return jenkinsClientProviderMap.get(instanceType.getBeanName());
    }

    /**
     * 该方法目前获取的jenkinsclient 主要用于构，和其它用途的区分开来
     *
     * @param instanceType
     * @return
     */
    public JenkinsClient getBuildJenkinsClient(InstanceType instanceType) {
        return buildJenkinsClientProviderMap.get(instanceType.getBeanName());
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

    public HttpClientBuilder clientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(5 * 1000);
        builder.setSocketTimeout(20 * 1000);
        builder.setConnectTimeout(5 * 1000);
        httpClientBuilder.setDefaultRequestConfig(builder.build());
        return httpClientBuilder;
    }

    protected JenkinsClient createJenkinsClient(String androidUrl, String username, String password) {
        JenkinsHttpClient httpClient = new JenkinsHttpClient(URI.create(androidUrl),
                clientBuilder,
                username, password);
        JenkinsServer androidJenkinsServer = new JenkinsServer(httpClient);
        return new JenkinsClient(androidJenkinsServer, httpClient);
    }

    protected abstract boolean shouldUpdateJenkinsClient();

    protected abstract void retrieveJenkinsClient();
}
