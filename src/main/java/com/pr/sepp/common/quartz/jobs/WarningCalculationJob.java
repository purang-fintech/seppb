package com.pr.sepp.common.quartz.jobs;

import com.pr.sepp.common.calculation.service.WarningCalculation;
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
public class WarningCalculationJob extends QuartzJobBean {

	@Autowired
	private WarningCalculation warningCalculation;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) {
		log.info("===========研发过程告警计算开始于{}===========", new Date());
		try {
			warningCalculation.warningCalculation();
		} catch (Exception e) {
			log.warn("研发过程告警计算发生异常！", e);
		}
		log.info("===========研发过程告警计算结束于{}===========", new Date());
	}
}