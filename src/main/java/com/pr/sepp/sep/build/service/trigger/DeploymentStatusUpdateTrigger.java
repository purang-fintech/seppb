package com.pr.sepp.sep.build.service.trigger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class DeploymentStatusUpdateTrigger extends AbstractStatusTrigger implements StatusTimer {

    private Duration checkPeriod = Duration.ofSeconds(10);
    private ScheduledFuture<?> scheduledTask;
    private Updater<DeploymentStatusUpdater> updater;

    public DeploymentStatusUpdateTrigger(Updater<DeploymentStatusUpdater> updater, ThreadPoolTaskScheduler taskScheduler) {
        super(taskScheduler);
        this.updater = updater;
    }


    @EventListener
    @Order()
    @Override
    public void onApplicationReady(ApplicationReadyEvent event) {
        schedule();
    }

    @EventListener
    @Order()
    @Override
    public void onClosedContext(ContextClosedEvent event) {
        if (event.getApplicationContext().getParent() == null ||
                "bootstrap".equals(event.getApplicationContext().getParent().getId())) {
            stopUpdateStatus();
        }
    }

    @Override
    public void stopUpdateStatus() {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(true);
            log.debug("Canceled update status task-deployment");
        }
    }

    @Override
    public void schedule() {
        if (scheduledTask == null || scheduledTask.isDone()) {
            scheduledTask = taskScheduler.scheduleAtFixedRate(() -> updater.update(DeploymentStatusUpdater::updateResult),
                    checkPeriod);
            log.debug("Scheduled update status task-deployment for every {}seconds", scheduledTask);
        }
    }
}
