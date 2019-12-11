package com.pr.sepp.sep.coding.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"spliterName", "responserName", "moduleName", "statusName"})
public class CodeMission {

	private Integer id;
	private Integer moduleId;
	private Integer reqId;
	private Integer status;
	private Float manpower;
	private Integer spliter;
	private String splitDate;
	private Integer responser;
	private String planBegin;
	private String planTo;
	private String attachment;
	private String summary;
	private String detail;

	private String reqDesc;
	private String spliterName;
	private String responserName;
	private String moduleName;
	private String statusName;
}