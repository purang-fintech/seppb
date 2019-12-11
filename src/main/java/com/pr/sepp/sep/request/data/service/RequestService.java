package com.pr.sepp.sep.request.data.service;

import com.pr.sepp.sep.request.audit.model.RequestAudit;
import com.pr.sepp.sep.request.data.model.ProductRequirement;

import java.util.List;
import java.util.Map;

public interface RequestService {

	List<ProductRequirement> requestQuery(Map<String, Object> dataMap);

	int requestCreate(ProductRequirement productRequirement);

	int requestUpdate(ProductRequirement productRequirement);

	int requestClose(Integer id);

	int requestSendAudit(RequestAudit requestAudit);

	int addAuditRefuseTimes(Integer id);
}
