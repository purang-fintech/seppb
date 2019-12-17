package com.pr.sepp.reports.monthly.controller;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.reports.monthly.service.MonthlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class MonthlyController {

	@Autowired
	public MonthlyService monthlyService;

	@RequestMapping(value = "/reports/monthDefectCount", method =  RequestMethod.POST)
	public List<Map<String, Object>> monthDefectCount(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.QRY_TIME_BEGIN, request.getParameter(CommonParameter.QRY_TIME_BEGIN));
		dataMap.put(CommonParameter.QRY_TIME_END, request.getParameter(CommonParameter.QRY_TIME_END));

		return monthlyService.monthDefectCount(dataMap);
	}

	@RequestMapping(value = "/reports/monthReqCount", method =  RequestMethod.POST)
	public List<Map<String, Object>> monthReqCount(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.QRY_TIME_BEGIN, request.getParameter(CommonParameter.QRY_TIME_BEGIN));
		dataMap.put(CommonParameter.QRY_TIME_END, request.getParameter(CommonParameter.QRY_TIME_END));

		return monthlyService.monthReqCount(dataMap);
	}

	@RequestMapping(value = "/reports/monthDefectCost", method =  RequestMethod.POST)
	public List<Map<String, Object>> monthDefectCost(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.QRY_TIME_BEGIN, request.getParameter(CommonParameter.QRY_TIME_BEGIN));
		dataMap.put(CommonParameter.QRY_TIME_END, request.getParameter(CommonParameter.QRY_TIME_END));

		return monthlyService.monthDefectCost(dataMap);
	}

	@RequestMapping(value = "/reports/monthReqCost", method =  RequestMethod.POST)
	public List<Map<String, Object>> monthReqCost(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.QRY_TIME_BEGIN, request.getParameter(CommonParameter.QRY_TIME_BEGIN));
		dataMap.put(CommonParameter.QRY_TIME_END, request.getParameter(CommonParameter.QRY_TIME_END));

		return monthlyService.monthReqCost(dataMap);
	}

	@RequestMapping(value = "/reports/reqCostTrend", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqCostTrend(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.QRY_TIME_BEGIN, request.getParameter(CommonParameter.QRY_TIME_BEGIN));
		dataMap.put(CommonParameter.QRY_TIME_END, request.getParameter(CommonParameter.QRY_TIME_END));

		return monthlyService.reqCostTrend(dataMap);
	}

	@RequestMapping(value = "/reports/defectCostTrendMonth", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectCostTrendM(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.QRY_TIME_BEGIN, request.getParameter(CommonParameter.QRY_TIME_BEGIN));
		dataMap.put(CommonParameter.QRY_TIME_END, request.getParameter(CommonParameter.QRY_TIME_END));

		return monthlyService.defectCostTrendM(dataMap);
	}

	@RequestMapping(value = "/reports/defectCostTrendRel", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectCostTrendR(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.QRY_TIME_BEGIN, request.getParameter(CommonParameter.QRY_TIME_BEGIN));
		dataMap.put(CommonParameter.QRY_TIME_END, request.getParameter(CommonParameter.QRY_TIME_END));

		return monthlyService.defectCostTrendR(dataMap);
	}

	@RequestMapping(value = "/reports/reqChangeTrend", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqChangeTrend(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.QRY_TIME_BEGIN, request.getParameter(CommonParameter.QRY_TIME_BEGIN));
		dataMap.put(CommonParameter.QRY_TIME_END, request.getParameter(CommonParameter.QRY_TIME_END));

		return monthlyService.reqChangeTrend(dataMap);
	}
}
