package com.pr.sepp.sep.testing.controller;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.sep.testing.model.CaseResult;
import com.pr.sepp.sep.testing.model.TestRun;
import com.pr.sepp.sep.testing.model.TestScenario;
import com.pr.sepp.sep.testing.service.TestExecService;
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
public class TestExecController {
	@Autowired
	private TestExecService testExecService;

	@RequestMapping(value = "/scenario/query", method = RequestMethod.POST)
	public List<TestScenario> testScenarioQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, request.getParameter(CommonParameter.ID));
		dataMap.put(CommonParameter.REL_ID, request.getParameter(CommonParameter.REL_ID));
		dataMap.put("planId", request.getParameter("planId"));
		dataMap.put("planType", request.getParameter("planType"));
		return testExecService.testScenarioQuery(dataMap);
	}

	@RequestMapping(value = "/scenario/create", method = RequestMethod.POST)
	public int testScenarioCreate(@RequestBody TestScenario scenario) {
		return testExecService.testScenarioCreate(scenario);
	}

	@RequestMapping(value = "/scenario/update", method = RequestMethod.POST)
	public int testScenarioUpdate(@RequestBody TestScenario scenario) throws IllegalAccessException {
		return testExecService.testScenarioUpdate(scenario);
	}

	@RequestMapping(value = "/scenario/delete/{id}", method = RequestMethod.POST)
	public int testScenarioDelete(@PathVariable(CommonParameter.ID) Integer id) {
		return testExecService.testScenarioDelete(id);
	}

	@RequestMapping(value = "/scenario/case_query/{scenarioId}", method = RequestMethod.POST)
	public List<Map<String, Object>> scenarioCaseQuery(@PathVariable Integer scenarioId) {
		if (null == scenarioId) {
			return null;
		}

		Map<String, Object> scenarioMap = new HashMap<>();
		scenarioMap.put(CommonParameter.ID, scenarioId);
		List<TestScenario> scenarios = testExecService.testScenarioQuery(scenarioMap);

		if (null == scenarios || scenarios.size() == 0) {
			return null;
		}
		if (StringUtils.isEmpty(scenarios.get(0).getCases())) {
			return null;
		}

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("cases", Arrays.asList(scenarios.get(0).getCases().split(",")));
		dataMap.put("scenarioId", scenarioId);
		return testExecService.scenarioCaseQuery(dataMap);
	}

	@RequestMapping(value = "/exec/history/{caseId}", method = RequestMethod.POST)
	public List<Map<String, Object>> caseRunHisQuery(@PathVariable("caseId") Integer caseId) {
		return testExecService.caseRunHisQuery(caseId);
	}

	@RequestMapping(value = "/exec/begin", method = RequestMethod.POST)
	public int testRunBegin(@RequestBody TestRun testRun) {
		return testExecService.testRunBegin(testRun);
	}

	@RequestMapping(value = "/exec/cancel", method = RequestMethod.POST)
	public int stepRunCancel(@RequestBody TestRun testRun) {
		return testExecService.testRunCancel(testRun);
	}

	@RequestMapping(value = "/exec/record_case_result", method = RequestMethod.POST)
	public int caseResultRecord(@RequestBody CaseResult caseResult) {
		return testExecService.caseResultRecord(caseResult);
	}

	@RequestMapping(value = "/exec/record_case_cancel", method = RequestMethod.POST)
	public int caseRunCancel(@RequestBody CaseResult caseResult) {
		return testExecService.caseRunCancel(caseResult);
	}

	@RequestMapping(value = "/exec/record_step_result", method = RequestMethod.POST)
	public int stepResultRecord(@RequestBody CaseResult caseResult) {
		return testExecService.stepResultRecord(caseResult);
	}
}
