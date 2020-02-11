package com.pr.sepp.common.calculation.model;

import lombok.Data;

@Data
public class ReleaseSepData {
	private Integer relId;
	private String warnDate;
	private String sitBeginDate;
	private String uatBeginDate;
	private String relDate;

	// defect data grouped
	private Long expectDefect;
	private Long foundDefect;
	private Long fixedDefect;
	private Long closedDefect;
	private Long noneReqDefect;
	private Long noneModuleDefect;
	private Long reqMaxDefect;
	private Long moduleMaxDefect;
	private Double reqAvgDefect;
	private Double moduleAvgDefect;

	// defect max fix cost and max fix times
	private Double fixCost;
	private Long fixTimes;

	// SIT and UAT test plans
	private Long sitPlanId;
	private Long uatPlanId;
	private Double sitPlaned;
	private Double sitExecuted;
	private Double uatPlaned;
	private Double uatExecuted;

	// code missions delayed and total code mission count
	private Long cmsDelayed;
	private Long cmsTotal;
}
