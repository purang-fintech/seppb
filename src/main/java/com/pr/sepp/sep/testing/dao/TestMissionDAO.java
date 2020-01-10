package com.pr.sepp.sep.testing.dao;

import com.pr.sepp.sep.testing.model.TestMission;

import java.util.List;
import java.util.Map;

public interface TestMissionDAO {
	int testMissionCreate(TestMission testMission);

	int testMissionUpdate(TestMission testMission);

	int testMissionStatusUpdate(Integer id, Integer status);

	int testMissionPlanUpdate(Map<String, Object> dataMap);

	int testMissionDelete(int id);

	int clearTestPlanMission(int planId);

	List<TestMission> testMissionQuery(Map<String, Object> dataMap);

	List<TestMission> notPlanedTMQuery(Map<String, Object> dataMap);

	List<TestMission> planTMSQuery(int planId);
}
