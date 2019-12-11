package com.pr.sepp.mgr.index.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.pr.sepp.mgr.index.dao.IndexDAO;
import com.pr.sepp.mgr.index.service.IndexService;

@Transactional
@Service("indexService")
public class IndexServiceImpl implements IndexService {

	@Autowired
	private IndexDAO indexDAO;

	@Override
	public List<Map<String, Object>> relRequestQuery(Map<String, String> dataMap) {
		return indexDAO.relRequestQuery(dataMap);
	}

	@Override
	public List<Map<String, Object>> relMissionQuery(Map<String, String> dataMap) {
		return indexDAO.relMissionQuery(dataMap);
	}

	@Override
	public List<Map<String, Object>> relDefectsQuery(Map<String, String> dataMap) {
		return indexDAO.relDefectsQuery(dataMap);
	}

	@Override
	public Map<String, Integer> relTestQuery(Map<String, String> dataMap) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();

		List<Map<String, Object>> scenarioCases = indexDAO.relScenarioQuery(dataMap);
		List<Map<String, Object>> textExec = new ArrayList<Map<String, Object>>();
		Map<String, Object> secondMap = new HashMap<>();
		int caseCount = 0;
		int passCount = 0;

		for (int i = 0; i < scenarioCases.size(); i++) {
			Map<String, Object> item = scenarioCases.get(i);
			if (StringUtils.isEmpty(item.get("cases"))) {
				continue;
			}
			String[] cases = String.valueOf(item.get("cases")).split(",");
			secondMap.put("scenarioId", item.get("scenarioId"));
			secondMap.put("cases", Arrays.asList(cases));
			textExec = indexDAO.relCaseExecQuery(secondMap);
			caseCount += cases.length;
			passCount += textExec.size();
		}

		resultMap.put("caseCount", caseCount);
		resultMap.put("passCount", passCount);
		return resultMap;
	}

}
