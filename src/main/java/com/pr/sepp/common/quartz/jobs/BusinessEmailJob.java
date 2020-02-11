package com.pr.sepp.common.quartz.jobs;

import com.pr.sepp.notify.message.service.MessageMailService;
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
public class BusinessEmailJob extends QuartzJobBean {

	@Autowired
	private MessageMailService mailService;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		log.info("===========邮件发送定时任务执行开始于{}===========", new Date());
		mailService.sendMail();
		log.info("===========邮件发送定时任务执行结束于{}===========", new Date());
	}
}