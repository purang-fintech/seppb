package com.pr.sepp.sep.change.controller;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.change.model.Change;
import com.pr.sepp.sep.change.service.ChangeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class ChangeController {
	@Autowired
	private ChangeService changeService;

	@RequestMapping(value = "/change/query", method = RequestMethod.POST)
	public List<Change> changeQuery(@RequestParam(value = "reqId") int reqId) {
		return changeService.changeQuery(reqId);
	}

	@RequestMapping(value = "/change/audit_query", method = RequestMethod.POST)
	public PageInfo<Change> changeAuditQuery(HttpServletRequest request) {

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("reqId", request.getParameter("reqId"));
		dataMap.put("cmId", request.getParameter("cmId"));
		dataMap.put("tmId", request.getParameter("tmId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("changeStatus", request.getParameter("changeStatus"));
		dataMap.put("changeUser", request.getParameter("changeUser"));
		dataMap.put("auditUser", request.getParameter("auditUser"));
		dataMap.put("audittedUser", request.getParameter("audittedUser"));
		if (!StringUtils.isEmpty(request.getParameter("changeTimeBegin"))) {
			dataMap.put("changeTimeBegin", request.getParameter("changeTimeBegin") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("changeTimeEnd"))) {
			dataMap.put("changeTimeEnd", request.getParameter("changeTimeEnd") + " 23:59:59");
		}

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<Change> list = changeService.changeAuditQuery(dataMap);
		PageInfo<Change> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/change/update/{id}", method = RequestMethod.POST)
	public synchronized int changeInfoUpdate(@PathVariable("id") Integer id) {
		return changeService.changeInfoUpdate(id);
	}

	@RequestMapping(value = "/change/on_way", method = RequestMethod.POST)
	public int changeOnWay(HttpServletRequest request) {
		int productId = ParameterThreadLocal.getProductId();
		String auditorRoles = changeService.changeAuditorRolesQuery(productId);
		if (StringUtils.isEmpty(auditorRoles)) {
			return 0;
		}

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("reqId", request.getParameter("reqId"));
		dataMap.put("cmId", request.getParameter("cmId"));
		dataMap.put("tmId", request.getParameter("tmId"));
		dataMap.put("relId", request.getParameter("relId"));
		return changeService.changeOnWay(dataMap);
	}
}
