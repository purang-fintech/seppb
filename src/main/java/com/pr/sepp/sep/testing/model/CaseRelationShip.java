package com.pr.sepp.sep.testing.model;

import lombok.Data;

@Data
public class CaseRelationShip {
	private Integer caseId;
	private Integer id;
	private Integer relateType;
	private Integer reqId;
	private String caseIds;
	private String ids;
}