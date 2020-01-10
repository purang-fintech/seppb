package com.pr.sepp.sep.request.data.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.request.audit.model.RequestAudit;
import com.pr.sepp.sep.request.data.model.ProductRequirement;
import com.pr.sepp.sep.request.data.service.RequestService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@ResponseBody
public class RequestController {

	@Autowired
	public RequestService requestsService;

	@PostMapping(value = "/request/create/{moduleIds}")
	public List<Integer> requestCreate(@RequestBody ProductRequirement productRequirement, @PathVariable("moduleIds") String moduleIds) {
		List<Integer> result = new ArrayList<>();
		Arrays.asList(moduleIds.split(",")).forEach(item -> {
			productRequirement.setProductId(ParameterThreadLocal.getProductId());
			productRequirement.setModuleId(Integer.parseInt(item));
			result.add(requestsService.requestCreate(productRequirement));
		});
		return result;
	}

	@PostMapping(value = "/request/update")
	public int requestUpdate(@RequestBody ProductRequirement productRequirement) {
		return requestsService.requestUpdate(productRequirement);
	}

	@PostMapping(value = "/request/close/{id}")
	public int requestClose(@PathVariable(CommonParameter.ID) Integer id) {
		return requestsService.requestClose(id);
	}

	@PostMapping(value = "/request/send_audit")
	public int requestSendAudit(@RequestBody RequestAudit requestAudit) {
		return requestsService.requestSendAudit(requestAudit);
	}

	@PostMapping(value = "/request/query")
	public PageInfo<ProductRequirement> requestQuery(HttpServletRequest request) {

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, request.getParameter(CommonParameter.ID));
		dataMap.put("priority", request.getParameter("priority"));
		String moduleIds = request.getParameter("moduleIds");
		if (!StringUtils.isEmpty(moduleIds)) {
			dataMap.put("modules", Arrays.asList(moduleIds.split(",")));
		}
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.SUBMITTER, request.getParameter(CommonParameter.SUBMITTER));
		if (!StringUtils.isEmpty(request.getParameter("submitDateBegintart"))) {
			dataMap.put("submitDateBegin", request.getParameter("submitDateBegintart") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("submitDateEnd"))) {
			dataMap.put("submitDateEnd", request.getParameter("submitDateEnd") + " 23:59:59");
		}
		String status = request.getParameter(CommonParameter.STATUS);
		if (!StringUtils.isEmpty(status)) {
			dataMap.put("sts", Arrays.asList(status.split(",")));
		}
		String type = request.getParameter("type");
		if (!StringUtils.isEmpty(type)) {
			dataMap.put("types", Arrays.asList(type.split(",")));
		}

		int pageNum = ParameterThreadLocal.getPageNum();
		int pageSize = ParameterThreadLocal.getPageSize();
		PageHelper.startPage(pageNum, pageSize);

		List<ProductRequirement> requests = requestsService.requestQuery(dataMap);
		PageInfo<ProductRequirement> pageInfo = new PageInfo<>(requests);
		return pageInfo;
	}
}
