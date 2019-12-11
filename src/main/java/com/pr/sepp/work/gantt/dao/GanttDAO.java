package com.pr.sepp.work.gantt.dao;

import java.util.List;
import java.util.Map;

public interface GanttDAO {
	
	public List<Map<String, Object>> ganttMissionQuery(Map<String, Object> dataMap);
	
}
