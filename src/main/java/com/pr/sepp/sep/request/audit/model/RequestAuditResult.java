package com.pr.sepp.sep.request.audit.model;

import lombok.Data;

@Data
public class RequestAuditResult {
	private Integer auditor;
	private Integer passed;
	private String auditTime;
	private String auditComment;
	private String auditName;
	private String newChiefAuditor;
}