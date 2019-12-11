package com.pr.sepp.sep.testing.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"relCode", "productName"})
public class TestReport {
	private Integer id;// 报告 ID
	private Integer productId;// 产品 ID
	private Integer relId;// 版本号ID
	private Integer planId;// 测试计划ID
	private Integer planType;// 计划类型
	private Integer reportType;// 报告类型
	private String reportDate;// 报告时间
	private String title;// 报告标题
	private String emergencyPlan;// 应急预案
	private String sqaSuggestion;// SQA分析建议
	private String url;// 报告存储位置
	private String relCode;// 版本编号
	private String productName;// 产品名称
	private String emailTo;// 报告住送人
	private String emailCc;// 报告抄送人
	private String planBegin;// 计划开始
	private String planEnd;// 计划结束
}