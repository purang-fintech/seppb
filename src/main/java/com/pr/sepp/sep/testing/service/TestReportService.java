package com.pr.sepp.sep.testing.service;

import com.pr.sepp.sep.testing.model.TestReport;

import java.util.List;
import java.util.Map;

public interface TestReportService {

	List<TestReport> reportListQuery(Map<String, Object> dataMap);

	List<TestReport> reportInfoQuery(Map<String, Object> dataMap);

	int testReportCreate(TestReport testReport);

	int testReportUpdate(TestReport testReport) throws IllegalAccessException;

	Map<String, Object> reportSumQuery(Map<String, Object> dataMap);
}
