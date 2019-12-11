package com.pr.sepp.common.quartz;

import lombok.Data;

@Data
public class JobInfo {
	private String jobName;
	private String groupName;
	private String description;
	private String cron;
	private String className;
	private String status;
}
