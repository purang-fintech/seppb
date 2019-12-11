package com.pr.sepp.sep.build.service.trigger;

import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class JenkinsStatusUpdateTrigger implements InitializingBean, Destroyable {

    private final ThreadPoolTaskScheduler taskScheduler;
    private Duration checkPeriodOne = Duration.ofSeconds(10);
    private Duration checkPeriodTow = Duration.ofMinutes(30);
    private volatile ScheduledFuture<?> scheduledTaskOne;
    private volatile ScheduledFuture<?> scheduledTaskTow;
    private JenkinsStatusUpdater jenkinsStatusUpdater;
    private JenkinsProperties jenkinsProperties;

    public JenkinsStatusUpdateTrigger(JenkinsStatusUpdater jenkinsStatusUpdater,
                                      JenkinsProperties jenkinsProperties) {
        this.jenkinsProperties = jenkinsProperties;
        this.jenkinsStatusUpdater = jenkinsStatusUpdater;
        this.taskScheduler = createThreadPoolTaskScheduler();
    }

    private static ThreadPoolTaskScheduler createThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(3);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setBeanName("jenkins-update-pool");
        taskScheduler.setErrorHandler(e -> log.error("error:", e));
        return taskScheduler;
    }

    @EventListener
    @Order()
    public void onApplicationReady(ApplicationReadyEvent event) {
        if (jenkinsProperties.isEnableSyncStatus()) {
            updateStatus();
        }
    }

    @EventListener
    @Order()
    public void onClosedContext(ContextClosedEvent event) {
        if (event.getApplicationContext().getParent() == null ||
                "bootstrap".equals(event.getApplicationContext().getParent().getId())) {
            stopUpdateStatus();
        }
    }

    private void updateStatus() {
        if (scheduledTaskOne == null || scheduledTaskOne.isDone()) {
            scheduledTaskOne = taskScheduler.scheduleAtFixedRate(jenkinsStatusUpdater::updateJobStatus, checkPeriodOne);
            log.debug("Scheduled update status task1 for every {}seconds", checkPeriodOne);
        }
        if (scheduledTaskTow == null || scheduledTaskTow.isDone()) {
            scheduledTaskTow = taskScheduler.scheduleAtFixedRate(jenkinsStatusUpdater::updateWholeJobStatus, checkPeriodTow);
            log.debug("Scheduled update status task2 for every {}minutes", checkPeriodTow);
        }
    }

    public void stopUpdateStatus() {
        if (scheduledTaskOne != null && !scheduledTaskOne.isDone()) {
            scheduledTaskOne.cancel(true);
            log.debug("Canceled update status task1");
        }

        if (scheduledTaskTow != null && !scheduledTaskTow.isDone()) {
            scheduledTaskTow.cancel(true);
            log.debug("Canceled update status task2");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        taskScheduler.afterPropertiesSet();
    }

    @Override
    public void destroy() throws DestroyFailedException {
        taskScheduler.destroy();
    }
}
