package com.pr.sepp.common.quartz.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class BusinessJobExample extends QuartzJobBean {

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		log.info("===========任务执行开始于{}===========", new Date());
		log.info("===========running===========");
		log.info("===========任务执行结束于{}===========", new Date());
	}
}