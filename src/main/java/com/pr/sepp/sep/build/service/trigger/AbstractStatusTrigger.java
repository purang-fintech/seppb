package com.pr.sepp.sep.build.service.trigger;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.security.auth.DestroyFailedException;

public abstract class AbstractStatusTrigger implements InitializingBean, DisposableBean {

    protected final ThreadPoolTaskScheduler taskScheduler;


    public abstract void onApplicationReady(ApplicationReadyEvent event);

    public abstract void onClosedContext(ContextClosedEvent event);

    public abstract void stopUpdateStatus();

    public AbstractStatusTrigger(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
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
