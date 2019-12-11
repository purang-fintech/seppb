package com.pr.sepp.common.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 为了简化管理(本应用无复杂场景支持需求)，trigger的group和name都使用job的对应属性拼接固定字符串
 * 这样同一个job只能对应一个trigger，不支持一个job在不同cron下执行，所以请妥善运用cron
 * 如需实现同一个JOB设置多trigger实现在支持多cron，请自行改造所有涉及trigger的地方，使用循环处理:
 * List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
 */

@Slf4j
@Service
public class QuartzManageUtils {

	@Autowired
	private Scheduler scheduler;

	private static final String TRG_SUFFIX = "_Trigger";

	public List<JobInfo> listAllJobs() {
		List<JobInfo> jobInfoList = new ArrayList<>();
		try {
			for (String groupName : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					String jobName = jobKey.getName();
					JobDetail jobDetail = scheduler.getJobDetail(jobKey);
					JobInfo jobInfo = new JobInfo();

					jobInfo.setGroupName(groupName);
					jobInfo.setJobName(jobName);
					jobInfo.setClassName(jobDetail.getJobClass().getName());
					jobInfo.setDescription(jobDetail.getDescription());

					TriggerKey triggerKey = TriggerKey.triggerKey(jobName + TRG_SUFFIX, groupName + TRG_SUFFIX);
					CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
					Trigger.TriggerState status = getJobState(jobName, groupName);
					jobInfo.setCron(cronTrigger.getCronExpression());
					jobInfo.setStatus(status.toString());
					jobInfoList.add(jobInfo);
				}
			}
		} catch (SchedulerException e) {
			log.error("获取定时任务列表出错", e);
			return null;
		}
		return jobInfoList;
	}

	public String addJob(String jobName, String groupName, String desc, String cronExp, Class<? extends Job> clazz) {
		if (!CronExpression.isValidExpression(cronExp)) {
			log.warn("创建定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，表达式错误：[{}]", cronExp);
			return "创建定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，表达式错误：[" + cronExp + "]";
		}
		try {
			if (isExistJob(jobName, groupName)) {
				log.warn("创建定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，任务已存在");
				return "创建定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，任务已存在";
			}
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp);
			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobName, groupName).withDescription(desc).storeDurably().build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(jobName + TRG_SUFFIX, groupName + TRG_SUFFIX)
					.withSchedule(cronScheduleBuilder)
					.build();
			scheduler.scheduleJob(jobDetail, trigger);
			log.info("创建定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功，定时执行时间：[{}]", cronExp);
			return "创建定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功";
		} catch (SchedulerException e) {
			log.error("创建定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，定时执行时间：[{}]", cronExp, e);
			return "创建定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败";
		}
	}

	public String updateJobCron(String jobName, String groupName, String cronExp) {
		if (!CronExpression.isValidExpression(cronExp)) {
			log.warn("修改定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，表达式错误：[{}]", cronExp);
			return "修改定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，表达式错误：[" + cronExp + "]";
		}
		try {
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp);
			TriggerKey tk = TriggerKey.triggerKey(jobName + TRG_SUFFIX, groupName + TRG_SUFFIX);
			Trigger trg = TriggerBuilder
					.newTrigger()
					.withIdentity(jobName + TRG_SUFFIX, groupName + TRG_SUFFIX)
					.withSchedule(cronScheduleBuilder).build();
			scheduler.rescheduleJob(tk, trg);
			log.info("修改定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功，修改执行时间：[{}]", cronExp);
			return "修改定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功";
		} catch (SchedulerException e) {
			log.error("修改定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，修改执行时间：[{}]", cronExp, e);
			return "修改定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败";
		}
	}

	public String deleteJob(String jobName, String groupName) {
		try {
			JobKey jobKey = JobKey.jobKey(jobName, groupName);
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName + TRG_SUFFIX, groupName + TRG_SUFFIX);
			if (!scheduler.checkExists(jobKey) || !scheduler.checkExists(triggerKey)) {
				log.error("删除定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，任务不存在！");
				return "删除定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，任务不存在！";
			}
			scheduler.pauseTrigger(triggerKey);
			scheduler.unscheduleJob(triggerKey);
			scheduler.deleteJob(jobKey);
			log.info("删除定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功，删除执行时间：[{}]", new Date());
			return "删除定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功";
		} catch (SchedulerException e) {
			log.error("删除定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，删除执行时间：[{}]", new Date(), e);
			return "删除定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败";
		}
	}

	public String runJob(String jobName, String groupName) {
		String message;
		try {
			JobKey jobKey = JobKey.jobKey(jobName, groupName);
			Trigger.TriggerState status = getJobState(jobName, groupName);
			if (Trigger.TriggerState.PAUSED.equals(status)) {
				scheduler.resumeJob(jobKey);
				scheduler.triggerJob(jobKey);
				message = "定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 原已暂停，现已恢复运行成功";
				log.info("定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 原已暂停，现已恢复运行成功，恢复执行时间：[{}]", new Date());
			} else if (Trigger.TriggerState.NONE.equals(status)) {
				message = "获取定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 运行状态失败";
				log.error("获取定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 运行状态失败");
			} else {
				scheduler.triggerJob(jobKey);
				message = "运行定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功";
				log.info("运行定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功，执行时间：[{}]", new Date());
			}
		} catch (SchedulerException e) {
			message = "运行定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败";
			log.error("运行定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，执行失败时间：[{}]", new Date(), e);
		}
		return message;
	}

	public String pauseJob(String jobName, String groupName) {
		try {
			JobKey jobKey = JobKey.jobKey(jobName, groupName);
			scheduler.pauseJob(jobKey);
			log.info("暂停定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功，暂停执行时间：[{}]", new Date());
			return "暂停定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功";
		} catch (SchedulerException e) {
			log.error("暂停定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，暂停执行时间：[{}]", new Date(), e);
			return "暂停定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败";
		}
	}

	public String pauseAllJob() {
		try {
			scheduler.pauseAll();
			log.info("暂停所有定时任务成功");
			return "暂停所有定时任务成功";
		} catch (SchedulerException e) {
			log.error("暂停所有定时任务失败");
			return "暂停所有定时任务失败";
		}
	}

	public String resumeJob(String jobName, String groupName) {
		try {
			JobKey jobKey = JobKey.jobKey(jobName, groupName);
			scheduler.resumeJob(jobKey);
			log.info("恢复定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功，恢复执行时间：[{}]", new Date());
			return "恢复定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 成功";
		} catch (SchedulerException e) {
			log.error("恢复定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败，恢复执行时间：[{}]", new Date(), e);
			return "恢复定时任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败";
		}
	}

	public String resumeAllJob() {
		try {
			scheduler.resumeAll();
			log.info("恢复所有定时任务成功，恢复执行时间：[{}]", new Date());
			return "恢复所有定时任务成功";
		} catch (SchedulerException e) {
			log.error("恢复所有定时任务失败，恢复执行时间：[{}]", new Date(), e);
			return "恢复所有定时任务失败";
		}
	}

	public boolean isExistJob(String jobName, String groupName) {
		try {
			JobKey jobKey = JobKey.jobKey(jobName, groupName);
			return scheduler.checkExists(jobKey);
		} catch (SchedulerException e) {
			log.error("检测任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败", e);
			return false;
		}
	}

	private Trigger.TriggerState getJobState(String jobName, String groupName) {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName + TRG_SUFFIX, groupName + TRG_SUFFIX);
			return scheduler.getTriggerState(triggerKey);
		} catch (SchedulerException e) {
			log.error("检测任务 [任务名称：" + jobName + " 任务组：" + groupName + "] 失败", e);
			return Trigger.TriggerState.NONE;
		}
	}
}