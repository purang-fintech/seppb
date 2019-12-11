package com.pr.sepp.sep.testing.service;

import java.util.List;
import java.util.Map;

import com.pr.sepp.sep.testing.model.PlanMissionReq;
import com.pr.sepp.sep.testing.model.TestMission;

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
