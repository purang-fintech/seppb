package com.pr.sepp.reports.fuzz.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.pr.sepp.sep.coding.model.CodeMission;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.requirement.model.Requirement;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.reports.fuzz.service.FuzzQueryService;
import com.pr.sepp.sep.problem.model.Problem;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@RestController
@ResponseBody
public class FuzzQueryController {

	@Autowired
	public FuzzQueryService fuzzQueryService;

	@RequestMapping(value = "/fuzz/release_query", method =  RequestMethod.POST)
	public PageInfo<Release> releaseQuery(HttpServletRequest request, @RequestParam(value = "searchText") String searchText) {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("searchText", searchText);

		int pageNum = StringUtils.isEmpty(request.getParameter("pageNum")) ? 1 : Integer.parseInt(request.getParameter("pageNum"));
		int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 500 : Integer.parseInt(request.getParameter("pageSize"));
		PageHelper.startPage(pageNum, pageSize);

		List<Release> list = fuzzQueryService.releaseQuery(dataMap);
		PageInfo<Release> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/fuzz/req_query", method =  RequestMethod.POST)
	public PageInfo<Requirement> reqQuery(HttpServletRequest request, @RequestParam(value = "searchText") String searchText) {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("searchText", searchText);

		int pageNum = StringUtils.isEmpty(request.getParameter("pageNum")) ? 1 : Integer.parseInt(request.getParameter("pageNum"));
		int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 500 : Integer.parseInt(request.getParameter("pageSize"));
		PageHelper.startPage(pageNum, pageSize);

		List<Requirement> list = fuzzQueryService.reqQuery(dataMap);
		PageInfo<Requirement> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/fuzz/cms_query", method =  RequestMethod.POST)
	public PageInfo<CodeMission> cmsQuery(HttpServletRequest request, @RequestParam(value = "searchText") String searchText) {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("searchText", searchText);

		int pageNum = StringUtils.isEmpty(request.getParameter("pageNum")) ? 1 : Integer.parseInt(request.getParameter("pageNum"));
		int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 500 : Integer.parseInt(request.getParameter("pageSize"));
		PageHelper.startPage(pageNum, pageSize);

		List<CodeMission> list = fuzzQueryService.cmsQuery(dataMap);
		PageInfo<CodeMission> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/fuzz/defect_query", method =  RequestMethod.POST)
	public PageInfo<Defect> defectQuery(HttpServletRequest request, @RequestParam(value = "searchText") String searchText) {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("searchText", searchText);

		int pageNum = StringUtils.isEmpty(request.getParameter("pageNum")) ? 1 : Integer.parseInt(request.getParameter("pageNum"));
		int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 500 : Integer.parseInt(request.getParameter("pageSize"));
		PageHelper.startPage(pageNum, pageSize);

		List<Defect> list = fuzzQueryService.defectQuery(dataMap);
		PageInfo<Defect> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/fuzz/problem_query", method =  RequestMethod.POST)
	public PageInfo<Problem> problemQuery(HttpServletRequest request, @RequestParam(value = "searchText") String searchText) {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("searchText", searchText);

		int pageNum = StringUtils.isEmpty(request.getParameter("pageNum")) ? 1 : Integer.parseInt(request.getParameter("pageNum"));
		int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 500 : Integer.parseInt(request.getParameter("pageSize"));
		PageHelper.startPage(pageNum, pageSize);

		List<Problem> list = fuzzQueryService.problemQuery(dataMap);
		PageInfo<Problem> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

}
