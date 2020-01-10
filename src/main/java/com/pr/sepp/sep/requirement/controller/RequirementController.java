package com.pr.sepp.sep.requirement.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.requirement.model.ReqStatusUpdate;
import com.pr.sepp.sep.requirement.model.Requirement;
import com.pr.sepp.sep.requirement.service.RequirementService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class RequirementController {

	@Autowired
	public RequirementService requirementService;

	@RequestMapping(value = "/req/query", method = RequestMethod.POST)
	public PageInfo<Requirement> reqQuery(HttpServletRequest request) {

		Map<String, Object> dataMap = new HashMap<>();
		// 前向兼容，解决前端可能会存在的语义不明的情况
		if (StringUtils.isNotEmpty(request.getParameter(CommonParameter.REQ_ID))) {
			dataMap.put(CommonParameter.ID, request.getParameter(CommonParameter.REQ_ID));
		} else if (StringUtils.isNotEmpty(request.getParameter(CommonParameter.ID))) {
			dataMap.put(CommonParameter.ID, request.getParameter(CommonParameter.ID));
		}
		dataMap.put(CommonParameter.REL_ID, request.getParameter(CommonParameter.REL_ID));
		dataMap.put("cmId", request.getParameter("cmId"));
		dataMap.put("tmId", request.getParameter("tmId"));
		dataMap.put("defectId", request.getParameter("defectId"));
		dataMap.put("priority", request.getParameter("priority"));
		String moduleIds = request.getParameter("moduleIds");
		if (!StringUtils.isEmpty(moduleIds)) {
			dataMap.put("modules", Arrays.asList(moduleIds.split(",")));
		}
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.SUBMITTER, request.getParameter(CommonParameter.SUBMITTER));
		if (!StringUtils.isEmpty(request.getParameter("submitDateBegin"))) {
			dataMap.put("submitDateBegin", request.getParameter("submitDateBegin") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("submitDateEnd"))) {
			dataMap.put("submitDateEnd", request.getParameter("submitDateEnd") + " 23:59:59");
		}
		if (!StringUtils.isEmpty(request.getParameter("expectDateBegin"))) {
			dataMap.put("expectDateBegin", request.getParameter("expectDateBegin") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("expectDateEnd"))) {
			dataMap.put("expectDateEnd", request.getParameter("expectDateEnd") + " 23:59:59");
		}
		if (!StringUtils.isEmpty(request.getParameter("sitBeginDateBegin"))) {
			dataMap.put("sitBeginDateBegin", request.getParameter("sitBeginDateBegin") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("sitBeginDateEnd"))) {
			dataMap.put("sitBeginDateEnd", request.getParameter("sitBeginDateEnd") + " 23:59:59");
		}
		String status = request.getParameter(CommonParameter.STATUS);
		if (!StringUtils.isEmpty(status)) {
			dataMap.put("sts", Arrays.asList(status.split(",")));
		}
		String type = request.getParameter("type");
		if (!StringUtils.isEmpty(type)) {
			dataMap.put("types", Arrays.asList(type.split(",")));
		}
		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());
		List<Requirement> list = requirementService.reqQuery(dataMap);
		PageInfo<Requirement> pageInfo = new PageInfo<>(list);

		return pageInfo;
	}

	@RequestMapping(value = "/req/batch_query/{reqIds}", method = RequestMethod.POST)
	public List<Requirement> reqBatchQuery(@PathVariable("reqIds") String reqIds) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("reqs", Arrays.asList(reqIds.split(",")));
		return requirementService.reqBatchQuery(dataMap);
	}

	@RequestMapping(value = "/req/release", method = RequestMethod.POST)
	public int reqRelease(@RequestBody String releasing) {
		return requirementService.reqRelease(JSON.parseObject(releasing).get("releasing").toString());
	}

	@RequestMapping(value = "/req/unrelease", method = RequestMethod.POST)
	public int reqUnRelease(@RequestBody String reqs) {
		List<String> reqIds = Arrays.asList(JSON.parseObject(reqs).get("reqs").toString().split(","));
		return requirementService.reqUnRelease(reqIds);
	}

	@RequestMapping(value = "/req/create", method = RequestMethod.POST)
	public int reqCreate(@RequestBody Requirement requirement) {
		return requirementService.reqCreate(requirement, 1);
	}

	@RequestMapping(value = "/req/update/{reqChanged}", method = RequestMethod.POST)
	public int reqUpdate(@RequestBody Requirement requirement, @PathVariable("reqChanged") Integer reqChanged) throws IllegalAccessException {
		return requirementService.reqUpdate(requirement, reqChanged);
	}

	@RequestMapping(value = "/req/status_update", method = RequestMethod.POST)
	public int reqStatusUpdate(@RequestBody ReqStatusUpdate reqStatusUpdate) {
		return requirementService.reqStatusUpdate(reqStatusUpdate);
	}

	@RequestMapping(value = "/req/history/{reqId}", method = RequestMethod.POST)
	public List<Map<String, String>> reqHistoryQuery(@PathVariable(CommonParameter.REQ_ID) Integer reqId) {
		return requirementService.reqHistoryQuery(reqId);
	}

	@RequestMapping(value = "/req/rel_query/{relId}/{pageNum}/{pageSize}", method = RequestMethod.POST)
	public PageInfo<Requirement> relReqQuery(@PathVariable(CommonParameter.REL_ID) Integer relId,
											 @PathVariable("pageNum") Integer pageNum,
											 @PathVariable("pageSize") Integer pageSize) {
		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());
		List<Requirement> list = requirementService.relReqQuery(relId, pageNum, pageSize);
		PageInfo<Requirement> pageInfo = new PageInfo<>(list);

		return pageInfo;
	}

	@RequestMapping(value = "/req/copy/{id}", method = RequestMethod.POST)
	public int reqDelayCopy(@PathVariable(CommonParameter.ID) Integer id) {
		return requirementService.reqDelayCopy(id);
	}
}
