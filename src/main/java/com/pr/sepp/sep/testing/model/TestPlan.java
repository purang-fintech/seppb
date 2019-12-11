package com.pr.sepp.sep.testing.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"submitterName", "responserName", "statusName", "typeName", "relCode"})
public class TestPlan {
	private Integer id;// 测试计划 ID
	private Integer relId;// 版本号
	private Integer submitter;// 测试计划创建人
	private Integer responser;// 测试计划负责人
	private Integer planStatus;// 测试计划状态:1打开，0关闭
	private Integer planType;// 测试计划类型
	private String planBegin;// 测试开始时间
	private String planEnd;// 测试结束时间
	private String reportDates;// 测试进度报告时间点，datetime格式，以逗号分隔
	private String emailTo;// 邮件主送
	private String emailCc;// 邮件抄送

	private String submitterName;// 测试计划提交人姓名
	private String responserName;// 测试计划负责人姓名
	private String statusName;// 测试计划状态名称
	private String typeName;// 测试计划类型名称
	private String relCode;// 测试计划所属版本
}