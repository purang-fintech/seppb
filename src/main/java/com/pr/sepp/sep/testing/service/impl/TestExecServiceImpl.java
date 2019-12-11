package com.pr.sepp.sep.testing.service.impl;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.sep.testing.dao.TestExecDAO;
import com.pr.sepp.sep.testing.dao.TestingDAO;
import com.pr.sepp.sep.testing.model.CaseResult;
import com.pr.sepp.sep.testing.model.TestRun;
import com.pr.sepp.sep.testing.model.TestScenario;
import com.pr.sepp.sep.testing.service.TestExecService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Transactional
@Service("testExecService")
public class TestExecServiceImpl implements TestExecService {

	@Autowired
	private TestExecDAO testExecDAO;

	@Autowired
	private TestingDAO testingDAO;

	@Autowired
	private HistoryService historyService;

	@Override
	public List<TestScenario> testScenarioQuery(Map<String, Object> dataMap) {
		return testExecDAO.testScenarioQuery(dataMap);
	}

	@Override
	public int testScenarioCreate(TestScenario scenario) {
		scenario.setCreator(ParameterThreadLocal.getUserId());
		testExecDAO.testScenarioCreate(scenario);

		SEPPHistory history = new SEPPHistory();
		history.setObjId(scenario.getId());
		history.setObjType(9);
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(1);
		history.setOperComment("创建测试用例集：" + scenario.getName());
		history.setObjKey("id");
		historyService.historyInsert(history);

		return scenario.getId();
	}

	@Override
	public int testScenarioUpdate(TestScenario scenario) throws IllegalAccessException {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("id", scenario.getId());
		TestScenario oldScenario = testExecDAO.testScenarioQuery(dataMap).get(0);

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends TestScenario> cls = scenario.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(scenario);
			Object oldValue = field.get(oldScenario);

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setOperUser(ParameterThreadLocal.getUserId());
				history.setOperType(2);
				history.setObjId(scenario.getId());
				history.setObjType(9);
				history.setProductId(ParameterThreadLocal.getProductId());
				history.setOperComment("更新测试用例集：" + scenario.getName());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return testExecDAO.testScenarioUpdate(scenario);
	}

	@Override
	public int testScenarioDelete(Integer id) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("id", id);
		TestScenario scenario = testExecDAO.testScenarioQuery(dataMap).get(0);

		SEPPHistory history = new SEPPHistory();
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setObjId(scenario.getId());
		history.setReferUser(scenario.getCreator());
		history.setObjType(9);
		history.setObjKey("id");
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperType(3);
		history.setOperComment("删除测试用例集：" + scenario.getName());
		historyService.historyInsert(history);

		return testExecDAO.testScenarioDelete(id);
	}

	@Override
	public List<Map<String, Object>> scenarioCaseQuery(Map<String, Object> dataMap) {
		return testExecDAO.scenarioCaseQuery(dataMap);
	}

	@Override
	public List<Map<String, Object>> caseRunHisQuery(Integer caseId) {
		List<Map<String, Object>> result = testExecDAO.caseRunHisQuery(caseId);
		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> data = result.get(i);
			String newDuration;
			String[] duration = data.get("runDuration").toString().split(":");
			if (duration.length < 2) {
				newDuration = data.get("runDuration").toString();
			} else {
				newDuration = duration[0] + "小时" + duration[1] + "分" + duration[2] + "秒";
			}
			data.put("runDuration", newDuration);
			result.remove(i);
			result.add(i, data);
		}
		return result;
	}

	@Override
	public int testRunBegin(TestRun testRun) {
		testExecDAO.testRunBegin(testRun);
		return testRun.getId();
	}

	@Override
	public int testRunCancel(TestRun testRun) {
		return testExecDAO.testRunEnd(testRun);
	}

	@Override
	public int caseResultRecord(CaseResult caseResult) {
		Map<String, Object> dataMap = new HashMap<>();
		Integer scenarioId = caseResult.getScenarioId();

		TestRun run = new TestRun();
		run.setScenarioId(scenarioId);
		run.setRunBeginTime(caseResult.getRunBeginTime());
		run.setRunEndTime(caseResult.getRunEndTime());

		testExecDAO.testRunBegin(run);

		List<Map<String, String>> cases = new Gson().fromJson(caseResult.getCaseResults(), new TypeToken<List<Map<String, String>>>() {
		}.getType());

		dataMap.put("scenarioId", scenarioId);
		dataMap.put("runId", run.getId());
		dataMap.put("cases", cases);
		dataMap.put("runBeginTime", caseResult.getRunBeginTime());

		int runs = testExecDAO.caseResultRecord(dataMap);

		testExecDAO.testRunEnd(run);

		for (int i = 0 ; i < cases.size(); i ++) {
			testingDAO.caseStatusUpdate(Integer.valueOf(cases.get(i).get("caseId")), 4);
		}
		return runs;
	}

	@Override
	public int caseRunCancel(CaseResult caseResult) {
		return testExecDAO.caseRunCancel(caseResult);
	}

	@Override
	public int stepResultRecord(CaseResult caseResult) {
		Map<String, Object> dataMap = new HashMap<>();
		List<Map<String, String>> steps = new Gson().fromJson(caseResult.getStepResults(), new TypeToken<List<Map<String, String>>>() {}.getType());

		dataMap.put("scenarioId", caseResult.getScenarioId());
		dataMap.put("runId", caseResult.getRunId());
		dataMap.put("runEndTime", caseResult.getRunEndTime());
		dataMap.put("steps", steps);

		TestRun run = new TestRun();
		run.setId(caseResult.getRunId());
		run.setRunEndTime(caseResult.getRunEndTime());

		testExecDAO.testRunEnd(run);

		testingDAO.caseStatusUpdate(Integer.valueOf(steps.get(0).get("caseId")), Integer.valueOf(steps.get(steps.size() - 1).get("result")));
		return testExecDAO.stepResultRecord(dataMap);
	}
}