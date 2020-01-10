package com.pr.sepp.sep.problem.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"statusName", "priorityName", "influenceName", "submitterName", "responserName",
		"typeFirstName", "typeSecondName", "resolveMethodName", "moduleName", "improveOneName", "improveTwoName"})
public class Problem {
	private Integer id;
	private Integer status;
	private Integer priority;
	private Integer influence;
	private Integer submitter;
	private Integer responser;
	private Integer typeFirst;
	private Integer typeSecond;
	private Integer resolveMethod;
	private Integer transId;
	private Integer productId;
	private Integer moduleId;
	private String summary;
	private String detail;
	private String attachment;
	private String submitTime;
	private String expectResolveTime;
	private String resolveTime;
	private String closeTime;
	private Float resolveCost;
	private Integer improveOne;
	private Integer improveTwo;
	private String improvePlanTo;
	private String improveDetail;
	private String refuseReason;

	private String statusName;
	private String priorityName;
	private String influenceName;
	private String submitterName;
	private String responserName;
	private String typeFirstName;
	private String typeSecondName;
	private String resolveMethodName;
	private String moduleName;
	private String improveOneName;
	private String improveTwoName;

}