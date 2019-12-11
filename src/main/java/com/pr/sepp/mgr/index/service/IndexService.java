package com.pr.sepp.mgr.index.service;

import java.util.List;
import java.util.Map;

public interface IndexService {

	public List<Map<String, Object>> relRequestQuery(Map<String, String> dataMap);

	public List<Map<String, Object>> relMissionQuery(Map<String, String> dataMap);

	public List<Map<String, Object>> relDefectsQuery(Map<String, String> dataMap);

	public Map<String, Integer> relTestQuery(Map<String, String> dataMap);

}
