package com.pr.sepp.sep.analysis.dao;

import java.util.List;
import java.util.Map;

public interface DataGraghDAO {

	public Map<String, Object> relInfoQuery(int relId);

	public List<Map<String, Object>> defectFound(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectResponse(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectFix(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectDeploy(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectVerify(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectModule(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectReqirements(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectFounder(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectResponser(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectPriority(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectInfluence(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectType(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectFoundPeriod(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectExpectPeriod(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectFixTimes(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectResponseCost(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectFixCost(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectDeployCost(Map<String, Object> dataMap);

	public List<Map<String, Object>> defectVerifyCost(Map<String, Object> dataMap);
}