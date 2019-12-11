package com.pr.sepp.reports.monthly.service;

import java.util.List;
import java.util.Map;

public interface MonthlyService {

	List<Map<String, Object>> monthDefectCount(Map<String, Object> dataMap);

	List<Map<String, Object>> monthReqCount(Map<String, Object> dataMap);

	List<Map<String, Object>> monthDefectCost(Map<String, Object> dataMap);

	List<Map<String, Object>> monthReqCost(Map<String, Object> dataMap);

	List<Map<String, Object>> reqCostTrend(Map<String, Object> dataMap);

	List<Map<String, Object>> reqChangeTrend(Map<String, Object> dataMap);

	List<Map<String, Object>> defectCostTrendM(Map<String, Object> dataMap);

	List<Map<String, Object>> defectCostTrendR(Map<String, Object> dataMap);
	
}
