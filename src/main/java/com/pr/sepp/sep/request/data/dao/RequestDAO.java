package com.pr.sepp.sep.request.data.dao;

import com.pr.sepp.sep.request.data.model.ProductRequirement;

import java.util.List;
import java.util.Map;

public interface RequestDAO {

	List<ProductRequirement> requestQuery(Map<String, Object> dataMap);

	int requestCreate(ProductRequirement productRequirement);

	int requestUpdate(ProductRequirement productRequirement);

	int requestStatusUpdate(Integer id, Integer status);

	int requestClose(Integer id);

	int requestSendAudit(Integer id);

	int addAuditRefuseTimes(Integer id);
}
