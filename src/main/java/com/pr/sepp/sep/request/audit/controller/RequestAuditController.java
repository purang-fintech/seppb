package com.pr.sepp.sep.request.audit.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.request.audit.model.RequestAudit;
import com.pr.sepp.sep.request.audit.model.RequestAuditResult;
import com.pr.sepp.sep.request.audit.service.RequestAuditService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class RequestAuditController {

	@Autowired
	public RequestAuditService requestAuditService;

	@PostMapping(value = "/request/audit_create")
	public int requestAuditCreate(@RequestBody RequestAudit requestAudit) {
		return requestAuditService.requestAuditCreate(requestAudit);
	}

	@PostMapping(value = "/request/audit/{id}")
	public int requestAudit(@RequestBody RequestAuditResult requestAuditResult, @PathVariable(CommonParameter.ID) Integer id) {
		return requestAuditService.requestAuditPush(id, requestAuditResult);
	}

	@PostMapping(value = "/request/audit_query")
	public PageInfo<RequestAudit> requestAuditQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, request.getParameter(CommonParameter.ID));
		dataMap.put("prId", request.getParameter("prId"));
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.SUBMITTER, request.getParameter(CommonParameter.SUBMITTER));
		dataMap.put("auditStatus", request.getParameter("auditStatus"));
		if (!StringUtils.isEmpty(request.getParameter("submitTimeStart"))) {
			dataMap.put("submitTimeStart", request.getParameter("submitTimeStart") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("submitTimeEnd"))) {
			dataMap.put("submitTimeEnd", request.getParameter("submitTimeEnd") + " 23:59:59");
		}

		int pageNum = ParameterThreadLocal.getPageNum();
		int pageSize = ParameterThreadLocal.getPageSize();
		PageHelper.startPage(pageNum, pageSize);

		List<RequestAudit> requestAudits = requestAuditService.requestAuditQuery(dataMap);
		PageInfo<RequestAudit> pageInfo = new PageInfo<>(requestAudits);
		return pageInfo;
	}
}
