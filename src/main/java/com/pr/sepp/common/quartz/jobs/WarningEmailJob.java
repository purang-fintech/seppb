package com.pr.sepp.common.quartz.jobs;

import com.pr.sepp.notify.warning.service.WarningMailService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WarningEmailJob extends QuartzJobBean {

	@Autowired
	private WarningMailService mailService;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		log.info("===========告警邮件发送定时任务执行开始于{}===========", new Date());
		mailService.sendWarningMail();
		log.info("===========告警邮件发送定时任务执行结束于{}===========", new Date());
	}
}