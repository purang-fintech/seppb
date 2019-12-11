package com.pr.sepp.sep.requirement.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"statusName", "typeName", "moduleName", "priorityName", "submitterName", "changeTimes",
		"cmCount", "changeDesc", "changeDetail", "proderName", "deverName", "testerName", "relCode"})
public class Requirement {
	private Integer id;
	private Integer productId;
	private Integer relId;
	private Integer sourceId;
	private Integer type;
	private Integer status;
	private Integer moduleId;
	private Integer priority;
	private Integer submitter;
	private String attachment;
	private String submitDate;
	private String expectDate;
	private String uatDate;
	private String sitDate;
	private String summary;
	private String uiResource;
	private String detail;
	private Integer closeStyle;
	private Integer pdResponser;
	private Integer devResponser;
	private Integer testResponser;

	private Integer changeTimes;
	private Integer cmCount;
	private String changeDesc;
	private String changeDetail;
	private String typeName;
	private String statusName;
	private String moduleName;
	private String priorityName;
	private String submitterName;
	private String proderName;
	private String deverName;
	private String testerName;
	private String relCode;
}