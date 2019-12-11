package com.pr.sepp.sep.testing.model;

import lombok.Data;

@Data
public class CaseStep {
	private Integer caseId;
	private Integer stepId;
	private String operation;
	private String inputData;
	private String expectResult;
}