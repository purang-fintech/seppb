package com.pr.sepp.sep.testing.service.impl;

import com.pr.sepp.base.dao.BaseQueryDAO;
import com.pr.sepp.base.model.TestPeriod;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.notify.model.Message;
import com.pr.sepp.notify.service.MessageService;
import com.pr.sepp.sep.release.dao.ReleaseDAO;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.testing.dao.TestMissionDAO;
import com.pr.sepp.sep.testing.dao.TestPlanDAO;
import com.pr.sepp.sep.testing.model.TestMission;
import com.pr.sepp.sep.testing.model.TestPlan;
import com.pr.sepp.sep.testing.service.TestPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Transactional
@Service("testPlanService")
public class TestPlanServiceImpl implements TestPlanService {

	@Autowired
	private TestPlanDAO testPlanDAO;

	@Autowired
	private TestMissionDAO testMissionDAO;

	@Autowired
	public ReleaseDAO releaseDAO;

	@Autowired
	BaseQueryDAO baseQueryDAO;

	@Autowired
	public MessageService messageService;

	@Autowired
	private HistoryService historyService;

	@Override
	public List<TestPlan> testPlanQuery(Map<String, Object> dataMap) {
		return testPlanDAO.testPlanQuery(dataMap);
	}

	@Override
	public synchronized int testPlanCreate(TestPlan testPlan) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int responser = testPlan.getResponser();
		int relId = testPlan.getRelId();
		int planType = testPlan.getPlanType();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.REL_ID, relId);
		dataMap.put(CommonParameter.PRODUCT_ID, productId);
		Release release = releaseDAO.releaseQuery(dataMap).get(0);
		List<TestPeriod> periods = baseQueryDAO.testPeriod();
		String testType = periods.stream().filter(a -> Objects.equals(a.getPeriodId(), planType)).findFirst().orElse(new TestPeriod()).getPeriodName();

		testPlanDAO.testPlanCreate(testPlan);
		int created = testPlan.getId();
		String msg = "版本【" + release.getRelCode() + "】下创建了【" + testType + "】计划";

		// 通知版本负责人
		List<Integer> messageTo = new ArrayList<>();
		messageTo.add(release.getResponser());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(7);
		message.setObjectId(created);
		message.setTitle("测试计划创建提示");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageTo);

		// 测试计划负责人的通知
		List<Integer> messageToRes = new ArrayList<>();
		messageToRes.add(responser);
		String suffix = "，并确定由您负责跟进";
		message.setContent(msg + suffix);
		messageService.businessMessageGenerator(message, userId, messageToRes);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(7);
		history.setObjKey(CommonParameter.ID);
		history.setObjId(created);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setReferUser(responser);
		history.setOperType(1);
		history.setNewValue(testPlan.toString());
		history.setOperComment(msg);
		historyService.historyInsert(history);

		return created;
	}

	@Override
	public int testPlanUpdate(TestPlan testPlan) throws IllegalAccessException {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int responser = testPlan.getResponser();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, testPlan.getId());
		TestPlan oldPlan = testPlanDAO.testPlanQuery(dataMap).get(0);

		testPlan.setSubmitter(oldPlan.getSubmitter());
		testPlan.setRelId(oldPlan.getRelId());
		testPlan.setPlanType(oldPlan.getPlanType());

		String msg = "版本【" + oldPlan.getRelCode() + "】下的【" + oldPlan.getTypeName() + "】计划已更新";
		boolean isNewOpen = testPlan.getPlanStatus() == 1;
		String suffix = testPlan.getPlanStatus() - oldPlan.getPlanStatus() == 0 ? ""
				: "，状态由【" + (isNewOpen ? "关闭" : "打开") + "】变为【" + (isNewOpen ? "打开" : "关闭") + "】";

		List<Integer> messageToSub = new ArrayList<>();
		messageToSub.add(oldPlan.getSubmitter());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(7);
		message.setObjectId(testPlan.getId());
		message.setTitle("测试计划更新提示");
		message.setContent(msg + suffix);
		messageService.businessMessageGenerator(message, userId, messageToSub);

		if (oldPlan.getResponser() - testPlan.getResponser() != 0) {
			if (testPlan.getResponser() != userId && testPlan.getResponser() - testPlan.getSubmitter() != 0) { //非测试计划负责人本人操作
				List<Integer> messageToResNew = new ArrayList<>();
				messageToResNew.add(testPlan.getResponser());
				messageService.businessMessageGenerator(message, userId, messageToResNew);
			}

			if (oldPlan.getResponser() - oldPlan.getSubmitter() != 0) { //非测试计划负责人本人操作
				List<Integer> messageToResOld = new ArrayList<>();
				messageToResOld.add(oldPlan.getResponser());
				suffix += "，并且转交给他人负责跟进";
				message.setContent(msg + suffix);
				messageService.businessMessageGenerator(message, userId, messageToResOld);
			}
		} else {
			if (oldPlan.getResponser() - oldPlan.getSubmitter() != 0) { //非测试计划负责人本人操作
				List<Integer> messageToRes = new ArrayList<>();
				messageToRes.add(oldPlan.getResponser());
				messageService.businessMessageGenerator(message, userId, messageToRes);
			}
		}

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends TestPlan> cls = testPlan.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(testPlan);
			Object oldValue = field.get(oldPlan);

			if (keyName.endsWith("Name")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(7);
				history.setObjId(testPlan.getId());
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment(msg);
				history.setReferUser(responser);
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return testPlanDAO.testPlanUpdate(testPlan);
	}

	@Override
	public int planTmsClose(Integer planId) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		List<TestMission> tms = testMissionDAO.planTMSQuery(planId);

		tms.forEach(item -> {
			String msg = "需求【#" + item.getReqId() + " - " + item.getReqSummary() + "】下的【"
					+ item.getTypeName() + "】任务随测试计划一同关闭";
			List<Integer> messageToSub = new ArrayList<>();
			messageToSub.add(item.getSpliter());

			Message message = new Message();
			message.setProductId(productId);
			message.setObjectType(18);
			message.setObjectId(planId);
			message.setTitle("测试任务关闭提示");
			message.setContent(msg);
			messageService.businessMessageGenerator(message, userId, messageToSub);

			if (item.getResponser() - item.getSpliter() != 0) { //非测试任务负责人本人操作
				List<Integer> messageToRes = new ArrayList<>();
				messageToRes.add(item.getResponser());
				messageService.businessMessageGenerator(message, userId, messageToRes);
			}
		});

		return testPlanDAO.planTmsClose(planId);
	}

	@Override
	public int testPlanDelete(Integer id) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, id);
		TestPlan oldPlan = testPlanDAO.testPlanQuery(dataMap).get(0);

		String msg = "版本【" + oldPlan.getRelCode() + "】下的【" + oldPlan.getTypeName() + "】计划被删除";

		List<Integer> messageToSub = new ArrayList<>();
		messageToSub.add(oldPlan.getSubmitter());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(7);
		message.setObjectId(id);
		message.setTitle("测试计划删除提示");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageToSub);

		if (!Objects.equals(oldPlan.getResponser(), oldPlan.getSubmitter())) { //非测试计划负责人本人操作
			List<Integer> messageToRes = new ArrayList<>();
			messageToRes.add(oldPlan.getResponser());
			messageService.businessMessageGenerator(message, userId, messageToRes);
		}

		testMissionDAO.clearTestPlanMission(id);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(7);
		history.setObjId(oldPlan.getId());
		history.setObjKey(CommonParameter.ID);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(3);
		history.setOperComment(msg);
		history.setReferUser(oldPlan.getSubmitter());
		historyService.historyInsert(history);
		history.setReferUser(oldPlan.getResponser());
		historyService.historyInsert(history);

		return testPlanDAO.testPlanDelete(id);
	}

	@Override
	public TestPlan latestPlanQuery(Integer planType) {
		return testPlanDAO.latestPlanQuery(planType, ParameterThreadLocal.getProductId());
	}

}