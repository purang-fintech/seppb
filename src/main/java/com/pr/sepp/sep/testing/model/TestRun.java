package com.pr.sepp.sep.testing.model;

import lombok.Data;

@Data
public class TestRun {
	private Integer id;// 测试运行ID
	private Integer scenarioId;// 测试集/场景ID
	private String runBeginTime;// 运行开始时间
	private String runEndTime;// 运行结束时间
}