package com.pr.sepp.sep.testing.controller;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.sep.testing.model.TestReport;
import com.pr.sepp.sep.testing.service.TestReportService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class TestReportController {
	@Autowired
	private TestReportService testReportService;

	@RequestMapping(value = "/report/query", method = RequestMethod.POST)
	public PageInfo<TestReport> reportListQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.REL_ID, request.getParameter(CommonParameter.REL_ID));
		dataMap.put("reportType", request.getParameter("reportType"));
		dataMap.put("planType", request.getParameter("planType"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<TestReport> list = testReportService.reportListQuery(dataMap);
		PageInfo<TestReport> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/report/create", method = RequestMethod.POST)
	public Integer testReportCreate(@RequestBody TestReport report) {
		return testReportService.testReportCreate(report);
	}

	@RequestMapping(value = "/report/update", method = RequestMethod.POST)
	public int testReportUpdate(@RequestBody TestReport report) throws IllegalAccessException {
		return testReportService.testReportUpdate(report);
	}

	@RequestMapping(value = "/report/info_query/{id}", method = RequestMethod.POST)
	public List<TestReport> reportInfoQuery(@PathVariable(CommonParameter.ID) String id) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, id);
		return testReportService.reportInfoQuery(dataMap);
	}

	@RequestMapping(value = "/report/sum_query", method = RequestMethod.POST)
	public Map<String, Object> reportSumQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.REL_ID, request.getParameter(CommonParameter.REL_ID));
		dataMap.put("planType", request.getParameter("planType"));
		dataMap.put("reportId", request.getParameter("reportId"));
		dataMap.put("reportDate", request.getParameter("reportDate"));

		return testReportService.reportSumQuery(dataMap);
	}
}