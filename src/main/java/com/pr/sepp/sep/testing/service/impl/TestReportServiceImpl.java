package com.pr.sepp.sep.testing.service.impl;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.sep.requirement.model.Requirement;
import com.pr.sepp.sep.requirement.service.RequirementService;
import com.pr.sepp.sep.testing.dao.TestReportDAO;
import com.pr.sepp.sep.testing.model.TestReport;
import com.pr.sepp.sep.testing.service.TestReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Transactional
@Service("testReportService")
public class TestReportServiceImpl implements TestReportService {

	@Autowired
	private TestReportDAO testReportDAO;

	@Autowired
	private RequirementService requirementService;

	@Autowired
	private HistoryService historyService;

	@Override
	public List<TestReport> reportListQuery(Map<String, Object> dataMap) {
		return testReportDAO.reportListQuery(dataMap);
	}

	@Override
	public List<TestReport> reportInfoQuery(Map<String, Object> dataMap) {
		return testReportDAO.reportInfoQuery(dataMap);
	}

	@Override
	public synchronized int testReportCreate(TestReport testReport) {
		if (null == testReport.getPlanId() && testReport.getPlanType() != 0) {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("relId", testReport.getRelId());
			dataMap.put("planType", testReport.getPlanType());
			Integer planId = testReportDAO.planSidQuery(dataMap);
			testReport.setPlanId(planId);
		}

		testReportDAO.testReportCreate(testReport);
		int created = testReport.getId();

		SEPPHistory history = new SEPPHistory();
		history.setObjType(8);
		history.setObjId(created);
		history.setObjKey("id");
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(1);
		history.setNewValue(testReport.getId() + "");
		history.setOperComment("生成测试报告：" + testReport.getTitle());
		historyService.historyInsert(history);

		return created;
	}

	@Override
	public int testReportUpdate(TestReport testReport) throws IllegalAccessException {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("id", testReport.getId());
		TestReport oldReport = testReportDAO.reportInfoQuery(dataMap).get(0);

		testReport.setId(oldReport.getId());
		testReport.setRelId(oldReport.getRelId());
		testReport.setPlanId(oldReport.getPlanId());
		testReport.setReportType(oldReport.getReportType());
		testReport.setReportDate(oldReport.getReportDate());
		testReport.setUrl(oldReport.getUrl());

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends TestReport> cls = testReport.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(testReport);
			Object oldValue = field.get(oldReport);

			if (keyName.endsWith("Name")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(8);
				history.setObjId(testReport.getId());
				history.setProductId(ParameterThreadLocal.getProductId());
				history.setOperUser(ParameterThreadLocal.getUserId());
				history.setOperType(2);
				history.setOperComment("更新测试报告内容：" + oldReport.getTitle());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		historyService.historyInsertBatch(histories);

		return testReportDAO.testReportUpdate(testReport);
	}

	@Override
	public Map<String, Object> reportSumQuery(Map<String, Object> dataMap) {
		Map<String, Object> resultMap = new HashMap<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date reportDate = null;

		if (null != dataMap.get("reportDate") && StringUtils.isNotEmpty(dataMap.get("reportDate").toString())) {
			try {
				reportDate = formatter.parse(dataMap.get("reportDate").toString());
			} catch (ParseException e) {
				log.error("解析报告日期发生错误", e.getMessage());
				e.printStackTrace();
			}
		}

		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put("productId", ParameterThreadLocal.getProductId());
		reqMap.put("relId", dataMap.get("relId"));
		List<Requirement> relReq = requirementService.reqQuery(reqMap);
		int coveredReq = testReportDAO.coveredReqQuery(dataMap);

		List<Defect> relDefect = testReportDAO.relDefectQuery(dataMap);

		String relCaseStr = testReportDAO.relCasesStrQuery(dataMap);

		List<String> cases;
		List<Map<String, Object>> relCases = new ArrayList<Map<String, Object>>();
		if (StringUtils.isNotEmpty(relCaseStr)) {
			cases = Arrays.asList(relCaseStr.split(","));
			Map<String, Object> caseMap = new HashMap<>();
			caseMap.put("cases", cases);
			relCases = testReportDAO.relCaseQuery(caseMap);
		}

		int runedCase = testReportDAO.runedCaseQuery(dataMap);
		int passedCase = testReportDAO.passedCaseQuery(dataMap);

		int totalDefect = relDefect.size();
		int lTop = 0;
		int lMid = 0;
		int lLow = 0;

		for (int i = 0; i < totalDefect; i++) {
			Defect bug = relDefect.get(i);
			if (null != reportDate) {
				try {
					if (bug.getStatus() == 0 && formatter.parse(bug.getClosedTime()).before(reportDate)) {
						continue;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				if (bug.getStatus() == 0) {
					continue;
				}
			}
			int level = bug.getInfluence();
			if (level < 3) {
				lTop++;
			}
			if (level == 3) {
				lMid++;
			}
			if (level > 3) {
				lLow++;
			}
		}

		resultMap.put("relReq", relReq);
		resultMap.put("totalReq", (null == relReq) ? 0 : relReq.size());
		resultMap.put("coveredReq", coveredReq);

		resultMap.put("relCase", relCases);
		resultMap.put("totalCase", (null == relCases) ? 0 : relCases.size());
		resultMap.put("runedCase", runedCase);
		resultMap.put("passedCase", passedCase);

		resultMap.put("relDefect", relDefect);
		resultMap.put("totalDefect", (null == relDefect) ? 0 : relDefect.size());
		resultMap.put("lTopNc", lTop);
		resultMap.put("lMidNc", lMid);
		resultMap.put("lLowNc", lLow);

		return resultMap;
	}

}