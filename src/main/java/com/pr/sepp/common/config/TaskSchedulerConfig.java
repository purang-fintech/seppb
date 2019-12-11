package com.pr.sepp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@Configuration
public class TaskSchedulerConfig {
    private final static ThreadPoolExecutor executor =
            new ThreadPoolExecutor(5, 20, 10, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(100),
                    r -> new Thread(r, "excelExportThread"),
                    new ThreadPoolExecutor.AbortPolicy());

    public static ThreadPoolExecutor getExecutor() {
        return executor;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(6);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setDaemon(true);
        return scheduler;
    }
}
