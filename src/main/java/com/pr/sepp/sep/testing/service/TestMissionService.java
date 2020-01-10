package com.pr.sepp.sep.testing.service;

import com.pr.sepp.sep.testing.model.PlanMissionReq;
import com.pr.sepp.sep.testing.model.TestMission;

import java.util.List;
import java.util.Map;

public interface TestMissionService {
	int testMissionCreate(TestMission testMission);

	int testMissionUpdate(TestMission testMission) throws IllegalAccessException;

	int testMissionStatusUpdate(Integer id, Integer status);

	int testMissionPlanUpdate(PlanMissionReq planMissionReq);

	int testMissionDelete(int id);

	int clearTestPlanMission(int planId);

	List<TestMission> testMissionQuery(Map<String, Object> dataMap);

	List<TestMission> notPlanedTMQuery(Map<String, Object> dataMap);

	List<TestMission> planTMSQuery(int planId);
}
