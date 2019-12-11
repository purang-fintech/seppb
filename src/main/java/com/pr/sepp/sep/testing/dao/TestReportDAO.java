package com.pr.sepp.sep.testing.dao;

import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.sep.testing.model.TestReport;

import java.util.List;
import java.util.Map;

public interface TestReportDAO {

	List<TestReport> reportListQuery(Map<String, Object> dataMap);

	List<TestReport> reportInfoQuery(Map<String, Object> dataMap);

	Integer testReportCreate(TestReport testReport);

	Integer testReportUpdate(TestReport testReport);

	Integer coveredReqQuery(Map<String, Object> dataMap);

	Integer runedCaseQuery(Map<String, Object> dataMap);

	Integer planSidQuery(Map<String, Object> dataMap);

	Integer passedCaseQuery(Map<String, Object> dataMap);

	String relCasesStrQuery(Map<String, Object> dataMap);

	List<Map<String, Object>> relCaseQuery(Map<String, Object> dataMap);

	List<Defect> relDefectQuery(Map<String, Object> dataMap);
}
