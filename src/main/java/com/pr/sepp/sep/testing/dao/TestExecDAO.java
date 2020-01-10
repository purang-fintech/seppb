package com.pr.sepp.sep.testing.dao;

import com.pr.sepp.sep.testing.model.CaseResult;
import com.pr.sepp.sep.testing.model.TestRun;
import com.pr.sepp.sep.testing.model.TestScenario;

import java.util.List;
import java.util.Map;

public interface TestExecDAO {

	List<TestScenario> testScenarioQuery(Map<String, Object> dataMap);

	int testScenarioCreate(TestScenario testExec);

	int testScenarioUpdate(TestScenario testExec);

	int testScenarioDelete(Integer id);

	List<Map<String, Object>> scenarioCaseQuery(Map<String, Object> dataMap);

	List<Map<String, Object>> caseRunHisQuery(Integer caseId);

	int testRunBegin(TestRun run);

	int testRunEnd(TestRun run);

	int caseResultRecord(Map<String, Object> dataMap);

	int stepResultRecord(Map<String, Object> dataMap);

	int caseRunCancel(CaseResult caseResult);
}
