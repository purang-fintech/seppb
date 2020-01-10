package com.pr.sepp.sep.build.service.trigger;

import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class JenkinsStatusUpdateTrigger extends AbstractStatusTrigger implements StatusTimer {

	private Duration checkPeriodOne = Duration.ofSeconds(10);
	private Duration checkPeriodTow = Duration.ofMinutes(30);
	private ScheduledFuture<?> scheduledTaskOne;
	private ScheduledFuture<?> scheduledTaskTow;
	private Updater<JenkinsStatusUpdater> updater;
	private JenkinsProperties jenkinsProperties;

	public JenkinsStatusUpdateTrigger(Updater<JenkinsStatusUpdater> updater,
									  JenkinsProperties jenkinsProperties,
									  ThreadPoolTaskScheduler taskScheduler) {
		super(taskScheduler);
		this.jenkinsProperties = jenkinsProperties;
		this.updater = updater;
	}

	@EventListener
	@Order()
	@Override
	public void onApplicationReady(ApplicationReadyEvent event) {
		if (jenkinsProperties.isEnableSyncStatus()) {
			schedule();
		}
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
	public void schedule() {
		if (scheduledTaskOne == null || scheduledTaskOne.isDone()) {
			scheduledTaskOne = taskScheduler.scheduleAtFixedRate(() -> updater.update(JenkinsStatusUpdater::updateSome), checkPeriodOne);
			log.debug("Scheduled update status task1 for every {}seconds", checkPeriodOne);
		}
		if (scheduledTaskTow == null || scheduledTaskTow.isDone()) {
			scheduledTaskTow = taskScheduler.scheduleAtFixedRate(() -> updater.update(JenkinsStatusUpdater::updateWhole), checkPeriodTow);
			log.debug("Scheduled update status task2 for every {}minutes", checkPeriodTow);
		}
	}

	@Override
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

}
