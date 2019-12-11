package com.pr.sepp.sqa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pr.sepp.sqa.service.AnalysisService;

@RestController
@ResponseBody
public class AnalysisController{

	@Autowired
	private AnalysisService analysisService;

	@RequestMapping(value = "/sqa/reqType", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqType(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.reqType(dataMap);
	}

	@RequestMapping(value = "/sqa/reqPriority", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqPriority(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.reqPriority(dataMap);
	}

	@RequestMapping(value = "/sqa/reqStatus", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqStatus(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.reqStatus(dataMap);
	}

	@RequestMapping(value = "/sqa/reqChange", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqChange(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.reqChange(dataMap);
	}

	@RequestMapping(value = "/sqa/reqModule", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqModule(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.reqModule(dataMap);
	}

	@RequestMapping(value = "/sqa/reqClose", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqClose(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.reqClose(dataMap);
	}

	@RequestMapping(value = "/sqa/reqSubmitter", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqSubmitter(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.reqSubmitter(dataMap);
	}

	@RequestMapping(value = "/sqa/reqOffset", method =  RequestMethod.POST)
	public List<Map<String, Object>> reqDevOffset(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.reqDevOffset(dataMap);
	}

	@RequestMapping(value = "/sqa/cmsSpliter", method =  RequestMethod.POST)
	public List<Map<String, Object>> cmsSpliter(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.cmsSpliter(dataMap);
	}

	@RequestMapping(value = "/sqa/cmsResponser", method =  RequestMethod.POST)
	public List<Map<String, Object>> cmsResponser(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.cmsResponser(dataMap);
	}

	@RequestMapping(value = "/sqa/cmsModule", method =  RequestMethod.POST)
	public List<Map<String, Object>> cmsModule(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.cmsModule(dataMap);
	}

	@RequestMapping(value = "/sqa/cmsStatus", method =  RequestMethod.POST)
	public List<Map<String, Object>> cmsStatus(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.cmsStatus(dataMap);
	}

	@RequestMapping(value = "/sqa/cmsManpower", method =  RequestMethod.POST)
	public List<Map<String, Object>> cmsManPower(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.cmsManPower(dataMap);
	}
	@RequestMapping(value = "/sqa/cmsOffset", method =  RequestMethod.POST)
	public List<Map<String, Object>> cmsDevOffset(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.cmsDevOffset(dataMap);
	}

	@RequestMapping(value = "/sqa/tmsSpliter", method =  RequestMethod.POST)
	public List<Map<String, Object>> tmsSpliter(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.tmsSpliter(dataMap);
	}

	@RequestMapping(value = "/sqa/tmsResponser", method =  RequestMethod.POST)
	public List<Map<String, Object>> tmsResponser(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.tmsResponser(dataMap);
	}

	@RequestMapping(value = "/sqa/tmsType", method =  RequestMethod.POST)
	public List<Map<String, Object>> tmsType(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.tmsType(dataMap);
	}

	@RequestMapping(value = "/sqa/tmsStatus", method =  RequestMethod.POST)
	public List<Map<String, Object>> tmsStatus(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.tmsStatus(dataMap);
	}

	@RequestMapping(value = "/sqa/tmsManpower", method =  RequestMethod.POST)
	public List<Map<String, Object>> tmsManPower(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.tmsManPower(dataMap);
	}
	@RequestMapping(value = "/sqa/tmsOffset", method =  RequestMethod.POST)
	public List<Map<String, Object>> tmsDevOffset(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.tmsDevOffset(dataMap);
	}

	@RequestMapping(value = "/sqa/bugDirection", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectDirection(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectDirection(dataMap);
	}

	@RequestMapping(value = "/sqa/bugFounder", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectFounder(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectFounder(dataMap);
	}

	@RequestMapping(value = "/sqa/bugResponser", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectResponser(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectResponser(dataMap);
	}

	@RequestMapping(value = "/sqa/bugModule", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectModule(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectModule(dataMap);
	}

	@RequestMapping(value = "/sqa/bugReq", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectReqirements(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectReqirements(dataMap);
	}

	@RequestMapping(value = "/sqa/bugPriority", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectPriority(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectPriority(dataMap);
	}

	@RequestMapping(value = "/sqa/bugInfluence", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectInfluence(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectInfluence(dataMap);
	}

	@RequestMapping(value = "/sqa/bugType", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectType(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectType(dataMap);
	}

	@RequestMapping(value = "/sqa/bugFoundPeriod", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectFoundPeriod(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectFoundPeriod(dataMap);
	}

	@RequestMapping(value = "/sqa/bugPeriod", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectProducePeriod(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectProducePeriod(dataMap);
	}

	@RequestMapping(value = "/sqa/bugFixtImes", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectFixTimes(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectFixTimes(dataMap);
	}

	@RequestMapping(value = "/sqa/bugFixCost", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectFixCost(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectFixCost(dataMap);
	}

	@RequestMapping(value = "/sqa/bugVerifyCost", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectVerifyCost(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("reqId", request.getParameter("reqId"));
		return analysisService.defectVerifyCost(dataMap);
	}

	@RequestMapping(value = "/sqa/bugReqDensity", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectReqDensity(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.defectReqDensity(dataMap);
	}

	@RequestMapping(value = "/sqa/bugManDensity", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectManDensity(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.defectManDensity(dataMap);
	}

	@RequestMapping(value = "/sqa/bugCaseDensity", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectCaseDensity(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.defectCaseDensity(dataMap);
	}

	@RequestMapping(value = "/sqa/bugCmsDensity", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectCmsDensity(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.defectCmsDensity(dataMap);
	}

	@RequestMapping(value = "/sqa/bugTmsDensity", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectTmsDensity(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.defectTmsDensity(dataMap);
	}

	@RequestMapping(value = "/sqa/bugReqDensityM", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectReqDensityM(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.defectReqDensityM(dataMap);
	}

	@RequestMapping(value = "/sqa/bugManDensityM", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectManDensityM(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.defectManDensityM(dataMap);
	}

	@RequestMapping(value = "/sqa/bugCmsDensityM", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectCmsDensityM(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.defectCmsDensityM(dataMap);
	}

	@RequestMapping(value = "/sqa/bugTmsDensityM", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectTmsDensityM(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.defectTmsDensityM(dataMap);
	}

	@RequestMapping(value = "/sqa/proSubmitter", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemSubmitter(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemSubmitter(dataMap);
	}

	@RequestMapping(value = "/sqa/proResponser", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemResponser(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemResponser(dataMap);
	}

	@RequestMapping(value = "/sqa/proModule", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemModule(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemModule(dataMap);
	}

	@RequestMapping(value = "/sqa/proStatus", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemStatus(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemStatus(dataMap);
	}

	@RequestMapping(value = "/sqa/proPriority", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemPriority(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemPriority(dataMap);
	}

	@RequestMapping(value = "/sqa/proInfluence", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemInfluence(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemInfluence(dataMap);
	}

	@RequestMapping(value = "/sqa/proTypeOne", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemTypeOne(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemTypeOne(dataMap);
	}

	@RequestMapping(value = "/sqa/proTypeTwo", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemTypeTwo(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemTypeTwo(dataMap);
	}

	@RequestMapping(value = "/sqa/proImproveOne", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemImproveOne(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemImproveOne(dataMap);
	}

	@RequestMapping(value = "/sqa/proImproveTwo", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemImproveTwo(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemImproveTwo(dataMap);
	}

	@RequestMapping(value = "/sqa/proResolveCost", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemResolveCost(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemResolveCost(dataMap);
	}

	@RequestMapping(value = "/sqa/proCloseCost", method =  RequestMethod.POST)
	public List<Map<String, Object>> problemCloseCost(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("timeType", request.getParameter("timeType"));
		dataMap.put("qTimeBegin", request.getParameter("qTimeBegin") + " 00:00:00");
		dataMap.put("qTimeEnd", request.getParameter("qTimeEnd") + " 23:59:59");
		return analysisService.problemCloseCost(dataMap);
	}
}