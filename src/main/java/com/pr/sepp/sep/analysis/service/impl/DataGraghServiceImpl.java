package com.pr.sepp.sep.analysis.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pr.sepp.sep.analysis.dao.DataGraghDAO;
import com.pr.sepp.sep.analysis.service.DataGraghService;
import com.pr.sepp.sep.testing.dao.TestPlanDAO;
import com.pr.sepp.sep.testing.model.TestPlan;

@Slf4j
@Transactional
@Service("dataGraghService")
public class DataGraghServiceImpl implements DataGraghService {

	@Autowired
	private DataGraghDAO dataGraghDAO;
	
	@Autowired
	private TestPlanDAO testPlanDAO;
	private static final String dateFormat = "yyyy-MM-dd";

	private List<String> getReleaseDates(int relId, int planType) {
		List<String> result = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		String planBegin, planEnd;
		
		if (planType == 0) {
			Map<String, Object> rel = dataGraghDAO.relInfoQuery(relId);
			planBegin = rel.get("sitBeginDate").toString();
			planEnd = rel.get("relDate").toString();
		} else {
			Map<String, Object> dataMap = new HashMap<> ();
			dataMap.put("relId", relId);
			dataMap.put("planType", planType);

			TestPlan testPlan = testPlanDAO.testPlanQuery(dataMap).get(0);
			planBegin = testPlan.getPlanBegin();
			planEnd = testPlan.getPlanEnd();
		}
		
		Date beginDate, endDate = null;
		try {
			beginDate = format.parse(planBegin);
			endDate = format.parse(planEnd);
			cal.setTime(beginDate);
			while (cal.getTime().before(endDate)) {
				result.add(format.format(cal.getTime()));
				cal.add(Calendar.DATE, 1);
			}
		} catch (ParseException e) {
			log.error("日期解析发生错误，版本计划数据错误", e.getMessage());
			e.printStackTrace();
		}
		result.add(planEnd);

		return result;
	}

	@Override
	public List<Map<String, Object>> defectDirection(int relId, int planType) {
		List<String> dates = getReleaseDates(relId, planType);
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", relId);
		dataMap.put("planType", planType);

		List<Map<String, Object>> defectFound = dataGraghDAO.defectFound(dataMap);
		List<Map<String, Object>> defectResponse = dataGraghDAO.defectResponse(dataMap);
		List<Map<String, Object>> defectFix = dataGraghDAO.defectFix(dataMap);
		List<Map<String, Object>> defectDeploy = dataGraghDAO.defectDeploy(dataMap);
		List<Map<String, Object>> defectVerify = dataGraghDAO.defectVerify(dataMap);
		List<Map<String, Object>> chartsData = new ArrayList<Map<String, Object>>();
		
		for (int i = 0; i < dates.size(); i ++) {
			Map<String, Object> chartsMap = new HashMap<>();
			String current = dates.get(i);
			chartsMap.put("date", current);
			chartsMap.put("dailyFound", 0);
			chartsMap.put("totalFound", 0);
			chartsMap.put("dailyResponse", 0);
			chartsMap.put("totalResponse", 0);
			chartsMap.put("dailyFixed", 0);
			chartsMap.put("totalFixed", 0);
			chartsMap.put("dailyDeployed", 0);
			chartsMap.put("totalDeployed", 0);
			chartsMap.put("dailyClosed", 0);
			chartsMap.put("totalClosed", 0);
			
			for (int j = 0; j < defectFound.size(); j ++) {
				Map<String, Object> found = defectFound.get(j);
				String foundTime = found.get("foundTime").toString();
				if (foundTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyFound", found.get("dailyFound"));
					chartsMap.put("totalFound", found.get("totalFound"));
					break;
				}
			}
			for (int k = 0; k < defectResponse.size(); k ++) {
				Map<String, Object> response = defectResponse.get(k);
				String responseTime = response.get("responseTime").toString();
				if (responseTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyResponse", response.get("dailyResponse"));
					chartsMap.put("totalResponse", response.get("totalResponse"));
					break;
				}
			}
			for (int l = 0; l < defectFix.size(); l ++) {
				Map<String, Object> fix = defectFix.get(l);
				String fixedTime = fix.get("fixedTime").toString();
				if (fixedTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyFixed", fix.get("dailyFixed"));
					chartsMap.put("totalFixed", fix.get("totalFixed"));
					break;
				}
			}
			for (int m = 0; m < defectDeploy.size(); m ++) {
				Map<String, Object> deploy = defectDeploy.get(m);
				String deployedTime = deploy.get("deployedTime").toString();
				if (deployedTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyDeployed", deploy.get("dailyDeployed"));
					chartsMap.put("totalDeployed", deploy.get("totalDeployed"));
					break;
				}
			}
			for (int m = 0; m < defectVerify.size(); m ++) {
				Map<String, Object> close = defectVerify.get(m);
				String closedTime = close.get("closedTime").toString();
				if (closedTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyClosed", close.get("dailyClosed"));
					chartsMap.put("totalClosed", close.get("totalClosed"));
					break;
				}
			}
			chartsData.add(chartsMap);
		}
		
		for (int i = 1; i < chartsData.size(); i ++) {
			Map<String, Object> chartsMap = chartsData.get(i);
			int totalFound = (int) chartsMap.get("totalFound");
			int totalResponse = (int) chartsMap.get("totalFound");
			int totalFixed = (int) chartsMap.get("totalFixed");
			int totalDeployed = (int) chartsMap.get("totalDeployed");
			int totalClosed = (int) chartsMap.get("totalClosed");
			
			totalFound += (totalFound == 0) ? (int) chartsData.get(i - 1).get("totalFound") : 0;
			totalResponse += (totalResponse == 0) ? (int) chartsData.get(i - 1).get("totalResponse") : 0;
			totalFixed += (totalFixed == 0) ? (int) chartsData.get(i - 1).get("totalFixed") : 0;
			totalDeployed += (totalDeployed == 0) ? (int) chartsData.get(i - 1).get("totalDeployed") : 0;
			totalClosed += (totalClosed == 0) ? (int) chartsData.get(i - 1).get("totalClosed") : 0;
			
			chartsMap.put("totalFound", totalFound);
			chartsMap.put("totalResponse", totalResponse);
			chartsMap.put("totalFixed", totalFixed);
			chartsMap.put("totalDeployed", totalDeployed);
			chartsMap.put("totalClosed", totalClosed);
			chartsData.remove(i);
			chartsData.add(i, chartsMap);
		}
		return chartsData;
	}

	@Override
	public Map<String, List<Map<String, Object>>> defectDistribution(int relId, int planType) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("relId", relId);
		dataMap.put("planType", planType);

		List<Map<String, Object>> defectModule = dataGraghDAO.defectModule(dataMap);
		List<Map<String, Object>> defectReqirements = dataGraghDAO.defectReqirements(dataMap);
		List<Map<String, Object>> defectFounder = dataGraghDAO.defectFounder(dataMap);
		List<Map<String, Object>> defectResponser = dataGraghDAO.defectResponser(dataMap);
		List<Map<String, Object>> defectInfluence = dataGraghDAO.defectInfluence(dataMap);
		List<Map<String, Object>> defectFixTimes = dataGraghDAO.defectFixTimes(dataMap);
		List<Map<String, Object>> defectFixCost = dataGraghDAO.defectFixCost(dataMap);
		List<Map<String, Object>> defectVerifyCost = dataGraghDAO.defectVerifyCost(dataMap);
		
		Map<String, List<Map<String, Object>>> chartsData = new HashMap<String, List<Map<String, Object>>>();
		chartsData.put("defectModule", defectModule);
		chartsData.put("defectReqirements", defectReqirements);
		chartsData.put("defectFounder", defectFounder);
		chartsData.put("defectResponser", defectResponser);
		chartsData.put("defectInfluence", defectInfluence);
		chartsData.put("defectFixTimes", defectFixTimes);
		chartsData.put("defectFixCost", defectFixCost);
		chartsData.put("defectVerifyCost", defectVerifyCost);
		
		return chartsData;
	}
}