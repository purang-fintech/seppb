package com.pr.sepp.utils.jenkins;

import com.pr.sepp.common.config.TaskSchedulerConfig;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

@Slf4j
public class JenkinsClientManager implements Runnable {

    private volatile boolean flag = true;
    private final JenkinsClientProvider jenkinsClientProvider;

    public JenkinsClientManager(JenkinsClientProvider jenkinsClientProvider) {
        this.jenkinsClientProvider = jenkinsClientProvider;
    }

    @Override
    public void run() {
        while (flag) {
            try {
                checkJenkinsHealth();
                Thread.sleep(30_000);
            } catch (Exception e) {
                log.warn("check health error:{}", e.getMessage());
            }
        }
    }

    public void checkJenkinsHealth() {
        log.debug("check jenkins client health start");
        Map<String, JenkinsClient> buildJenkinsClientProviderMap = jenkinsClientProvider.getBuildJenkinsClientProviderMap();
        Set<Map.Entry<String, JenkinsClient>> buildEntries = buildJenkinsClientProviderMap.entrySet();
        boolean available = isAvailable(buildEntries);
        Map<String, JenkinsClient> jenkinsClientProviderMap = jenkinsClientProvider.getJenkinsClientProviderMap();
        Set<Map.Entry<String, JenkinsClient>> queryEntries = jenkinsClientProviderMap.entrySet();
        boolean queryAvailable = isAvailable(queryEntries);
        if (!available || !queryAvailable) {
            jenkinsClientProvider.retrieveJenkinsClient();
        }
    }

    private boolean isAvailable(Set<Map.Entry<String, JenkinsClient>> entries) {
        boolean available = true;
        for (Map.Entry<String, JenkinsClient> entry : entries) {
            boolean running = entry.getValue().isRunning();
            if (!running) {
                log.debug("jenkins running error");
                available = false;
            }
        }
        return available;
    }


    @PostConstruct
    public void start() {
        TaskSchedulerConfig.getExecutor().execute(this);
    }
}
