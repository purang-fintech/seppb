package com.pr.sepp.mgr.index.dao;

import java.util.List;
import java.util.Map;

public interface IndexDAO {

	List<Map<String, Object>> relRequestQuery(Map<String, String> dataMap);

	List<Map<String, Object>> relMissionQuery(Map<String, String> dataMap);

	List<Map<String, Object>> relDefectsQuery(Map<String, String> dataMap);

	List<Map<String, Object>> relScenarioQuery(Map<String, String> dataMap);

	List<Map<String, Object>> relCaseExecQuery(Map<String, Object> dataMap);

}
