package com.pr.sepp.work.gantt.service;

import java.util.List;
import java.util.Map;

public interface GanttService {
	
	public List<Map<String, Object>> ganttMissionQuery(Map<String, Object> dataMap);
	
}
