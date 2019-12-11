package com.pr.sepp.sep.request.data.service.impl;

import com.pr.sepp.sep.request.audit.model.RequestAudit;
import com.pr.sepp.sep.request.audit.service.RequestAuditService;
import com.pr.sepp.sep.request.data.dao.RequestDAO;
import com.pr.sepp.sep.request.data.model.ProductRequirement;
import com.pr.sepp.sep.request.data.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RequestServiceImpl implements RequestService {

	@Autowired
	private RequestDAO requestDAO;

	@Autowired
	private RequestAuditService requestAuditService;

	@Override
	public List<ProductRequirement> requestQuery(Map<String, Object> dataMap) {
		return requestDAO.requestQuery(dataMap);
	}

	@Override
	public int requestCreate(ProductRequirement productRequirement) {
		return requestDAO.requestCreate(productRequirement);
	}

	@Override
	public int requestUpdate(ProductRequirement productRequirement) {
		return requestDAO.requestUpdate(productRequirement);
	}

	@Override
	public int requestClose(Integer id) {
		return requestDAO.requestClose(id);
	}

	@Override
	public int requestSendAudit(RequestAudit requestAudit) {
		requestAuditService.requestAuditCreate(requestAudit);
		return requestDAO.requestSendAudit(requestAudit.getPrId());
	}

	@Override
	public int addAuditRefuseTimes(Integer id) {
		return requestDAO.addAuditRefuseTimes(id);
	}
}
