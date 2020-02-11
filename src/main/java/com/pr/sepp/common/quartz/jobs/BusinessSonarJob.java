package com.pr.sepp.common.quartz.jobs;


import com.pr.sepp.utils.sonar.service.SonarService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class BusinessSonarJob extends QuartzJobBean {

    @Autowired
    private SonarService sonarService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        sonarService.syncSonarData();
    }
}
