package com.pr.sepp.sep.testing.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.testing.model.PlanMissionReq;
import com.pr.sepp.sep.testing.model.TestMission;
import com.pr.sepp.sep.testing.service.TestMissionService;
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
public class TestMissionController {

	@Autowired
	public TestMissionService testMissionService;

	@RequestMapping(value = "/tms/query", method = RequestMethod.POST)
	public PageInfo<TestMission> testMissionQuery(HttpServletRequest request) {

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		dataMap.put(CommonParameter.ID, request.getParameter(CommonParameter.ID));
		dataMap.put(CommonParameter.REQ_ID, request.getParameter(CommonParameter.REQ_ID));
		dataMap.put(CommonParameter.REL_ID, request.getParameter(CommonParameter.REL_ID));
		dataMap.put("planId", request.getParameter("planId"));
		dataMap.put("type", request.getParameter("type"));
		dataMap.put(CommonParameter.SPLITER, request.getParameter(CommonParameter.SPLITER));
		String status = request.getParameter(CommonParameter.STATUS);
		if (!StringUtils.isEmpty(status)) {
			dataMap.put("sts", Arrays.asList(status.split(",")));
		}
		dataMap.put(CommonParameter.RESPONSER, request.getParameter(CommonParameter.RESPONSER));
		if (!StringUtils.isEmpty(request.getParameter("splitDateBegin"))) {
			dataMap.put("splitDateBegin", request.getParameter("splitDateBegin") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("splitDateEnd"))) {
			dataMap.put("splitDateEnd", request.getParameter("splitDateEnd") + " 23:59:59");
		}
		if (!StringUtils.isEmpty(request.getParameter("planToBegin"))) {
			dataMap.put("planToBegin", request.getParameter("planToBegin") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("planToEnd"))) {
			dataMap.put("planToEnd", request.getParameter("planToEnd") + " 23:59:59");
		}

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<TestMission> list = testMissionService.testMissionQuery(dataMap);
		PageInfo<TestMission> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/tms/plan_query", method = RequestMethod.POST)
	public PageInfo<TestMission> planTMSQuery(HttpServletRequest request) {
		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<TestMission> list = testMissionService.planTMSQuery(Integer.parseInt(request.getParameter("planId")));
		PageInfo<TestMission> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/tms/nrel_query", method = RequestMethod.POST)
	public PageInfo<TestMission> notPlanedTMQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put("type", request.getParameter("type"));
		dataMap.put(CommonParameter.REL_ID, request.getParameter(CommonParameter.REL_ID));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<TestMission> list = testMissionService.notPlanedTMQuery(dataMap);
		PageInfo<TestMission> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/tms/create", method = RequestMethod.POST)
	public int testMissionCreate(@RequestBody TestMission testMission) {
		return testMissionService.testMissionCreate(testMission);
	}

	@RequestMapping(value = "/tms/update", method = RequestMethod.POST)
	public int testMissionUpdate(@RequestBody TestMission testMission) throws IllegalAccessException {
		return testMissionService.testMissionUpdate(testMission);
	}

	@RequestMapping(value = "/tms/status_update/{id}", method = RequestMethod.POST)
	public int testMissionStatusUpdate(@PathVariable(CommonParameter.ID) Integer id, @RequestBody String status) {
		return testMissionService.testMissionStatusUpdate(id, Integer.parseInt(JSON.parseObject(status).get(CommonParameter.STATUS).toString()));
	}

	@RequestMapping(value = "/tms/plan_update", method = RequestMethod.POST)
	public int testMissionPlanUpdate(@RequestBody PlanMissionReq planMissionReq) {
		return testMissionService.testMissionPlanUpdate(planMissionReq);
	}

	@RequestMapping(value = "/tms/delete/{id}", method = RequestMethod.POST)
	public int testMissionDelete(@PathVariable(CommonParameter.ID) int id) {
		return testMissionService.testMissionDelete(id);
	}

}
