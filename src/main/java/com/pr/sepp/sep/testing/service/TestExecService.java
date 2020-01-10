package com.pr.sepp.sep.testing.service;

import com.pr.sepp.sep.testing.model.CaseResult;
import com.pr.sepp.sep.testing.model.TestRun;
import com.pr.sepp.sep.testing.model.TestScenario;

import java.util.List;
import java.util.Map;

public interface TestExecService {

	List<TestScenario> testScenarioQuery(Map<String, Object> dataMap);

	int testScenarioCreate(TestScenario testExec);

	int testScenarioUpdate(TestScenario testExec) throws IllegalAccessException;

	int testScenarioDelete(Integer id);

	List<Map<String, Object>> scenarioCaseQuery(Map<String, Object> dataMap);

	List<Map<String, Object>> caseRunHisQuery(Integer caseId);

	int testRunBegin(TestRun run);

	int testRunCancel(TestRun run);

	int caseResultRecord(CaseResult caseResult);

	int stepResultRecord(CaseResult caseResult);

	int caseRunCancel(CaseResult caseResult);
}
