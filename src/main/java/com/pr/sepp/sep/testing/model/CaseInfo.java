package com.pr.sepp.sep.testing.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"designerName", "statusName", "priorityName", "testPeriodName", "testTypeName", "prodModuleName"})
public class CaseInfo {
	private Integer caseId;
	private Integer status;
	private Integer priority;
	private Integer designer;
	private Integer testPeriod;
	private Integer testType;
	private Integer prodModule;
	private String regressMark;
	private String autoPath;
	private Integer autoType;
	private String preCondition;
	private String name;
	private String summary;

	private String designerName;
	private String statusName;
	private String priorityName;
	private String testPeriodName;
	private String testTypeName;
	private String prodModuleName;
}
