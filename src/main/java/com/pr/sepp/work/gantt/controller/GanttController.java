package com.pr.sepp.work.gantt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pr.sepp.work.gantt.service.GanttService;

@RestController
@ResponseBody
public class GanttController {
	@Autowired
	private GanttService ganttService;

	@RequestMapping(value = "/gantt/mission_query", method =  RequestMethod.POST)
	public List<Map<String, Object>> ganttMissionQuery(HttpServletRequest request) throws Exception {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("planBeginBegin", request.getParameter("planBeginBegin") + " 00:00:00");
		dataMap.put("planBeginEnd", request.getParameter("planBeginEnd") + " 23:59:59");
		dataMap.put("planEndBegin", request.getParameter("planEndBegin") + " 00:00:00");
		dataMap.put("planEndEnd", request.getParameter("planEndEnd") + " 23:59:59");
		dataMap.put("productId", request.getParameter("productId"));

		return ganttService.ganttMissionQuery(dataMap);
	}
}
