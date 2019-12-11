package com.pr.sepp.sep.testing.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"spliterName", "responserName", "typeName", "statusName"})
public class TestMission {
	private Integer id;// 测试任务 ID
	private Integer status;// 测试任务状态
	private Integer reqId;// 所属需求ID
	private Integer planId;// 测试计划ID
	private Integer type;// 测试任务类型，如冒烟测试等
	private Integer spliter;// 测试任务拆分人ID
	private Integer responser;// 测试任务负责人ID
	private Float manpower;// 所需人力（人日）
	private String assistant;// 测试任务参与人
	private String splitDate;// 测试任务拆分时间
	private String planBegin;// 测试任务开始时间
	private String planTo;// 测试任务结束时间

	private String reqSummary;// 产品需求摘要
	private String spliterName;// 测试任务拆分人姓名
	private String responserName;// 测试任务负责人姓名
	private String typeName;// 测试任务类型名称
	private String statusName;// 测试任务状态名称
}