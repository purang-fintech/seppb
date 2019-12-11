package com.pr.sepp.common.quartz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class QuartzManageController {

	@Autowired
	private QuartzManageUtils quartzUtils;

	@RequestMapping(value = "/job/list", method = RequestMethod.POST)
	public List<JobInfo> listAllJobs() {
		return quartzUtils.listAllJobs();
	}

	@RequestMapping(value = "/job/create", method = RequestMethod.POST)
	public String addJob(@RequestBody JobInfo jobInfo) {
		Class jobClass;
		try {
			jobClass = Class.forName(jobInfo.getClassName());
		} catch (ClassNotFoundException e) {
			log.error("创建失败，任务类{}不存在", jobInfo.getClassName(), e);
			return String.format("创建失败，任务类{}不存在", jobInfo.getClassName());
		}
		return quartzUtils.addJob(jobInfo.getJobName(), jobInfo.getGroupName(), jobInfo.getDescription(), jobInfo.getCron(), jobClass);
	}

	@RequestMapping(value = "/job/edit", method = RequestMethod.POST)
	public String updateJobCron(@RequestBody JobInfo jobInfo) {
		return quartzUtils.updateJobCron(jobInfo.getJobName(), jobInfo.getGroupName(), jobInfo.getCron());
	}

	@RequestMapping(value = "/job/run", method = RequestMethod.POST)
	public String runJob(@RequestBody JobInfo jobInfo) {
		return quartzUtils.runJob(jobInfo.getJobName(), jobInfo.getGroupName());
	}

	@RequestMapping(value = "/job/pause", method = RequestMethod.POST)
	public String pauseJob(@RequestBody JobInfo jobInfo) {
		return quartzUtils.pauseJob(jobInfo.getJobName(), jobInfo.getGroupName());
	}

	@RequestMapping(value = "/job/resume", method = RequestMethod.POST)
	public String resumeJob(@RequestBody JobInfo jobInfo) {
		return quartzUtils.resumeJob(jobInfo.getJobName(), jobInfo.getGroupName());
	}

	@RequestMapping(value = "/job/delete", method = RequestMethod.POST)
	public String deleteJob(@RequestBody JobInfo jobInfo) {
		return quartzUtils.deleteJob(jobInfo.getJobName(), jobInfo.getGroupName());
	}

	@RequestMapping(value = "/job/pause_all", method = RequestMethod.POST)
	public String pauseAllJob() {
		return quartzUtils.pauseAllJob();
	}

	@RequestMapping(value = "/job/resume_all", method = RequestMethod.POST)
	public String resumeAllJob() {
		return quartzUtils.resumeAllJob();
	}
}
