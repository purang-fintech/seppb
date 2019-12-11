package com.pr.sepp.sep.request.data.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"typeName", "statusName", "moduleName", "priorityName", "submitterName"})
public class ProductRequirement {
	private Integer id;
	private String submitDate;
	private String expectDate;
	private Integer type;
	private Integer status;
	private Integer productId;
	private Integer moduleId;
	private Integer priority;
	private Integer submitter;
	private String attachment;
	private String summary;
	private String detail;
	private String uiResource;
	private Integer refuseTimes;

	private String typeName;
	private String statusName;
	private String moduleName;
	private String priorityName;
	private String submitterName;
}