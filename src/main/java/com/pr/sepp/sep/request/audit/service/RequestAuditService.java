package com.pr.sepp.sep.request.audit.service;

import com.pr.sepp.sep.request.audit.model.RequestAudit;
import com.pr.sepp.sep.request.audit.model.RequestAuditResult;

import java.util.List;
import java.util.Map;

public interface RequestAuditService {

	int requestAuditCreate(RequestAudit requestAudit);

	int requestAuditPush(int id, RequestAuditResult requestAuditResult);

	int requestAuditComplete(RequestAudit requestAudit, int passed, String completeTime);

	List<RequestAudit> requestAuditQuery(Map<String, Object> dataMap);
}
