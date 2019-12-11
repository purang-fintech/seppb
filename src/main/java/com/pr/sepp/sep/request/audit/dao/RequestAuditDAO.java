package com.pr.sepp.sep.request.audit.dao;

import com.pr.sepp.sep.request.audit.model.RequestAudit;

import java.util.List;
import java.util.Map;

public interface RequestAuditDAO {

	int requestAuditCreate(RequestAudit requestAudit);

	int requestAuditAppend(Integer id, String newChiefAuditor);

	int requestBaseAuditPush(Integer id, String baseAuditResult);

	int requestLeaderAuditPush(Integer id, String leaderAuditResult);

	int requestChiefAuditPush(Integer id, String chiefAuditResult);

	int requestAuditComplete(Integer id, Integer formalId, String completeTime);

	List<RequestAudit> requestAuditQuery(Map<String, Object> dataMap);
}
