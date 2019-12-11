package com.pr.sepp.history.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@RestController
@ResponseBody
public class HistoryController {
	@Autowired
	private HistoryService historyService;

	@RequestMapping(value = "/mgr/direct", method =  RequestMethod.POST)
	public PageInfo<SEPPHistory> historyDirectQuery(HttpServletRequest request) {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("userId", request.getParameter("userId"));
		dataMap.put("productId", request.getParameter("productId"));

		int pageNum = StringUtils.isEmpty(request.getParameter("pageNum")) ? 1 : Integer.parseInt(request.getParameter("pageNum"));
		int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 500 : Integer.parseInt(request.getParameter("pageSize"));
		PageHelper.startPage(pageNum, pageSize);

		List<SEPPHistory> list = historyService.historyDirectQuery(dataMap);
		PageInfo<SEPPHistory> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/mgr/refer", method =  RequestMethod.POST)
	public PageInfo<SEPPHistory> historyReferQuery(HttpServletRequest request) {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("userId", request.getParameter("userId"));
		dataMap.put("productId", request.getParameter("productId"));

		int pageNum = StringUtils.isEmpty(request.getParameter("pageNum")) ? 1 : Integer.parseInt(request.getParameter("pageNum"));
		int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 500 : Integer.parseInt(request.getParameter("pageSize"));
		PageHelper.startPage(pageNum, pageSize);

		List<SEPPHistory> list = historyService.historyReferQuery(dataMap);
		PageInfo<SEPPHistory> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}
}
