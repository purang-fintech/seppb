package com.pr.sepp.sep.request.audit.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(exclude = {"submitterName", "baseAuditorName", "leaderAuditorName", "chiefAuditorName", "reqSummary", "baseAuditResult", "leaderAuditResult", "chiefAuditResult"})
public class RequestAudit {
	private Integer id;
	private Integer prId;
	private Integer formalId;
	private Integer submitter;
	private String submitTime;
	private String auditDeadline;
	private String completeTime;
	private String baseAuditor;
	private String leaderAuditor;
	private String chiefAuditor;

	private String baseAuditorName;
	private String leaderAuditorName;
	private String chiefAuditorName;
	private String submitterName;
	private String reqSummary;

	private String baseAuditResultStr;
	private String leaderAuditResultStr;
	private String chiefAuditResultStr;
	private List<RequestAuditResult> baseAuditResult;
	private List<RequestAuditResult> leaderAuditResult;
	private List<RequestAuditResult> chiefAuditResult;
}