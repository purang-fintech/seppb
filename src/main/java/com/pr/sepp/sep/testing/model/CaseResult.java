package com.pr.sepp.sep.testing.model;

import lombok.Data;

@Data
public class CaseResult {
	private Integer runId;
	private Integer scenarioId;
	private Integer caseId;
	private Integer result;
	private String caseResults;
	private String stepResults;
	private String runBeginTime;
	private String runEndTime;
}