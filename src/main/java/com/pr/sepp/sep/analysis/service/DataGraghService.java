package com.pr.sepp.sep.analysis.service;

import java.util.List;
import java.util.Map;

public interface DataGraghService {

	public List<Map<String, Object>> defectDirection(int relId, int planType);
	
	public Map<String, List<Map<String, Object>>> defectDistribution(int relId, int planType);
}
