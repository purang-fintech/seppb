package com.pr.sepp.sep.defect.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"statusName", "priorityName", "influenceName", "submitterName", "responserName", "productorName", "relCode",
					"defectTypeName", "foundPeriodName", "foundMeansName", "defectPeriodName", "prodModuleName", "conciliatorName"})
public class Defect {
	private Integer id;
	private Integer status;
	private Integer reqId;
	private Integer relId;
	private Integer priority;
	private Integer influence;
	private Integer submitter;
	private Integer conciliator;
	private Integer responser;
	private Integer productor;
	private Integer defectType;
	private Integer foundPeriod;
	private Integer foundMeans;
	private Integer defectPeriod;
	private Integer productId;
	private Integer prodModule;
	private Integer fixTimes;
	private String detail;
	private String summary;
	private String foundTime;
	private String responseTime;
	private String fixedTime;
	private String deployedTime;
	private String closedTime;
	private Float responseCost;
	private Float fixCost;
	private Float deployCost;
	private Float verifyCost;
	private Integer refuseReason;
	private String refuseDetail;
	private Integer sameCodeDefect;

	private String relCode;
	private String statusName;
	private String priorityName;
	private String influenceName;
	private String submitterName;
	private String responserName;
	private String productorName;
	private String defectTypeName;
	private String foundPeriodName;
	private String foundMeansName;
	private String defectPeriodName;
	private String prodModuleName;
	private String conciliatorName;
}