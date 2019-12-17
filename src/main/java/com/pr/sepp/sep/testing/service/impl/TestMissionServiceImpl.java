package com.pr.sepp.sep.testing.service.impl;

import com.pr.sepp.base.dao.BaseQueryDAO;
import com.pr.sepp.base.model.TestMissionStatus;
import com.pr.sepp.base.model.TestPeriod;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.notify.model.Message;
import com.pr.sepp.notify.service.MessageService;
import com.pr.sepp.sep.requirement.dao.RequirementDAO;
import com.pr.sepp.sep.requirement.model.Requirement;
import com.pr.sepp.sep.testing.dao.TestMissionDAO;
import com.pr.sepp.sep.testing.dao.TestPlanDAO;
import com.pr.sepp.sep.testing.model.PlanMissionReq;
import com.pr.sepp.sep.testing.model.TestMission;
import com.pr.sepp.sep.testing.model.TestPlan;
import com.pr.sepp.sep.testing.service.TestMissionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Transactional
public class TestMissionServiceImpl implements TestMissionService {

	@Autowired
	private TestMissionDAO testMissionDAO;

	@Autowired
	public MessageService messageService;

	@Autowired
	private BaseQueryDAO baseQueryDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private TestPlanDAO testPlanDAO;

	@Autowired
	private RequirementDAO requirementDAO;

	@Autowired
	private HistoryService historyService;

	@Override
	public int testMissionCreate(TestMission testMission) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		String testType = baseQueryDAO.testPeriod().stream().filter(
				a -> Objects.equals(a.getPeriodId(), testMission.getType())).findFirst().orElse(new TestPeriod()).getPeriodName();

		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put(CommonParameter.PRODUCT_ID, productId);
		reqMap.put(CommonParameter.REQ_ID, testMission.getReqId());
		Requirement req = requirementDAO.reqQuery(reqMap).get(0);
		String reqSum = "【#" + req.getId() + " - " + req.getSummary() + "】";

		testMissionDAO.testMissionCreate(testMission);
		int createdId = testMission.getId();
		String msg = "为需求" + reqSum + "创建了【" + testType + "】任务";

		// 测试任务负责人的通知
		List<Integer> messageToRes = new ArrayList<>();
		messageToRes.add(testMission.getResponser());
		String suffix = "，并且指定由您负责主导完成";

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(18);
		message.setObjectId(createdId);
		message.setTitle("测试任务创建提示");
		message.setContent(msg + suffix);
		messageService.businessMessageGenerator(message, userId, messageToRes);

		// 测试任务协助人的通知
		if (StringUtils.isNotEmpty(testMission.getAssistant())) {
			Map<String, Object> assistMap = new HashMap();
			assistMap.put("ids", Arrays.asList(testMission.getAssistant().split(",")));
			List<User> assistants = userDAO.userQueryByIds(assistMap);

			List<Integer> messageToAss = new ArrayList<>();
			assistants.forEach(d -> {
				messageToAss.add(d.getUserId());
			});
			suffix = "，并且指定由您协助完成";

			message.setContent(msg + suffix);
			messageService.businessMessageGenerator(message, userId, messageToAss);
		}

		SEPPHistory history = new SEPPHistory();
		history.setObjType(18);
		history.setObjId(createdId);
		history.setObjKey(CommonParameter.ID);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(1);
		history.setNewValue(reqSum + "：【" + testType + "】任务");
		history.setOperComment(msg);
		historyService.historyInsert(history);

		return createdId;
	}

	@Override
	public int testMissionUpdate(TestMission testMission) throws IllegalAccessException {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		Map<String, Object> queryOld = new HashMap<>();
		queryOld.put(CommonParameter.ID, testMission.getId());
		queryOld.put(CommonParameter.PRODUCT_ID, productId);
		TestMission oldTestMission = testMissionDAO.testMissionQuery(queryOld).get(0);

		testMission.setReqId(oldTestMission.getReqId());
		testMission.setType(oldTestMission.getType());
		testMission.setSpliter(oldTestMission.getSpliter());
		testMission.setSplitDate(oldTestMission.getSplitDate());

		String reqSum = "【#" + oldTestMission.getReqId() + " - " + oldTestMission.getReqSummary() + "】";
		String msg = "需求" + reqSum + "下的【" + oldTestMission.getTypeName() + "】任务，内容信息被修改";

		List<Integer> messageToSub = new ArrayList<>();
		messageToSub.add(oldTestMission.getSpliter());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(18);
		message.setObjectId(testMission.getId());
		message.setTitle("测试任务信息修改提示");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageToSub);

		if (oldTestMission.getResponser() - testMission.getResponser() != 0) {
			if (testMission.getResponser() - testMission.getSpliter() != 0) { //非测试计划负责人本人操作
				List<Integer> messageToResNew = new ArrayList<>();
				messageToResNew.add(testMission.getResponser());
				message.setContent(msg);
				messageService.businessMessageGenerator(message, userId, messageToResNew);
			}
			if (oldTestMission.getResponser() - oldTestMission.getSpliter() != 0) { //非测试计划负责人本人操作
				List<Integer> messageToResOld = new ArrayList<>();
				messageToResOld.add(oldTestMission.getResponser());
				String suffix = "，并且转交给他人负责跟进";
				message.setContent(msg + suffix);
				messageService.businessMessageGenerator(message, userId, messageToResOld);
			}
		} else {
			if (oldTestMission.getResponser() - oldTestMission.getSpliter() != 0) { //非测试计划负责人本人操作
				List<Integer> messageToRes = new ArrayList<>();
				messageToRes.add(oldTestMission.getResponser());
				message.setContent(msg);
				messageService.businessMessageGenerator(message, userId, messageToRes);
			}
		}

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends TestMission> cls = testMission.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(testMission);
			Object oldValue = field.get(oldTestMission);

			if (keyName.endsWith("Name") || keyName.equals("reqSummary")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(18);
				history.setObjId(testMission.getId());
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment(msg);
				history.setReferUser(oldTestMission.getResponser());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return testMissionDAO.testMissionUpdate(testMission);
	}

	@Override
	public List<TestMission> testMissionQuery(Map<String, Object> dataMap) {
		return testMissionDAO.testMissionQuery(dataMap);
	}

	@Override
	public int testMissionStatusUpdate(Integer id, Integer status) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		List<TestMissionStatus> sts = baseQueryDAO.testMissionStatus();
		String newStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), status)).findFirst().orElse(new TestMissionStatus()).getStatusName();

		Map<String, Object> queryOld = new HashMap<>();
		queryOld.put(CommonParameter.ID, id);
		TestMission oldTestMission = testMissionDAO.testMissionQuery(queryOld).get(0);

		String reqSum = "【#" + oldTestMission.getReqId() + " - " + oldTestMission.getReqSummary() + "】";
		String msg = "需求" + reqSum + "下的【" + oldTestMission.getTypeName() + "】任务，状态由【"
				+ oldTestMission.getStatusName() + "】变为【" + newStatusName + "】";

		List<Integer> messageToSub = new ArrayList<>();
		messageToSub.add(oldTestMission.getSpliter());
		String prefix = "您创建的，";

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(18);
		message.setObjectId(id);
		message.setTitle("测试任务状态修改提示");
		message.setContent(prefix + msg);
		messageService.businessMessageGenerator(message, userId, messageToSub);

		if (!Objects.equals(oldTestMission.getResponser(), oldTestMission.getSpliter())) { //非测试任务负责人本人操作
			List<Integer> messageToRes = new ArrayList<>();
			messageToRes.add(oldTestMission.getResponser());
			prefix = "您负责的，";
			message.setContent(prefix + msg);
			messageService.businessMessageGenerator(message, userId, messageToRes);
		}

		SEPPHistory history = new SEPPHistory();
		history.setObjType(18);
		history.setObjId(id);
		history.setObjKey(CommonParameter.STATUS);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(2);
		history.setOrgValue(String.valueOf(oldTestMission.getStatus()));
		history.setNewValue(status + "");
		history.setOperComment(msg);
		history.setReferUser(oldTestMission.getResponser());
		historyService.historyInsert(history);

		return testMissionDAO.testMissionStatusUpdate(id, status);
	}

	@Override
	public int testMissionPlanUpdate(PlanMissionReq planMissionReq) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		List<String> tms = Arrays.asList(planMissionReq.getIds().split(","));

		Integer planId = planMissionReq.getPlanId();
		if (planId < 0) {
			planMissionReq.setPlanId(null);
		}

		tms.forEach(d -> {
			Map<String, Object> queryOld = new HashMap<>();
			queryOld.put(CommonParameter.ID, d);
			TestMission oldTestMission = testMissionDAO.testMissionQuery(queryOld).get(0);

			String suffix;
			String reqSum = "【#" + oldTestMission.getReqId() + " - " + oldTestMission.getReqSummary() + "】";
			String msg = "需求" + reqSum + "下的【" + oldTestMission.getTypeName() + "】任务";

			if (null == planMissionReq.getPlanId()) {
				Map<String, Object> planMap = new HashMap<>();
				planMap.put(CommonParameter.ID, oldTestMission.getPlanId());
				TestPlan plan = testPlanDAO.testPlanQuery(planMap).get(0);
				suffix = "从版本【" + plan.getRelCode() + "】的【" + oldTestMission.getTypeName() + "】计划中移除";
			} else {
				Map<String, Object> planMap = new HashMap<>();
				planMap.put(CommonParameter.ID, planMissionReq.getPlanId());
				TestPlan plan = testPlanDAO.testPlanQuery(planMap).get(0);
				suffix = "纳入版本【" + plan.getRelCode() + "】的【" + oldTestMission.getTypeName() + "】计划中";
			}

			List<Integer> messageToSub = new ArrayList<>();
			messageToSub.add(oldTestMission.getSpliter());

			Message message = new Message();
			message.setProductId(productId);
			message.setObjectType(18);
			message.setObjectId(Integer.parseInt(d));
			message.setTitle("测试任务与测试计划关联");
			message.setContent(msg + suffix);
			messageService.businessMessageGenerator(message, userId, messageToSub);

			if (!Objects.equals(oldTestMission.getResponser(), oldTestMission.getSpliter())) {
				List<Integer> messageToRes = new ArrayList<>();
				messageToRes.add(oldTestMission.getResponser());
				messageService.businessMessageGenerator(message, userId, messageToRes);
			}
		});

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("ids", tms);
		dataMap.put("planId", planMissionReq.getPlanId());
		
		return testMissionDAO.testMissionPlanUpdate(dataMap);
	}

	@Override
	public int testMissionDelete(int id) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		Map<String, Object> queryOld = new HashMap<>();
		queryOld.put(CommonParameter.ID, id);
		TestMission oldTestMission = testMissionDAO.testMissionQuery(queryOld).get(0);

		String reqSum = "【#" + oldTestMission.getReqId() + " - " + oldTestMission.getReqSummary() + "】";
		String msg = "需求" + reqSum + "下的【" + oldTestMission.getTypeName() + "】任务被删除";

		List<Integer> messageToSub = new ArrayList<>();
		messageToSub.add(oldTestMission.getSpliter());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(18);
		message.setObjectId(id);
		message.setTitle("测试任务删除提示");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageToSub);

		if (!Objects.equals(oldTestMission.getResponser(), oldTestMission.getSpliter())) { //非测试任务负责人本人操作
			List<Integer> messageToRes = new ArrayList<>();
			messageToRes.add(oldTestMission.getResponser());
			messageService.businessMessageGenerator(message, userId, messageToRes);
		}

		SEPPHistory history = new SEPPHistory();
		history.setObjType(18);
		history.setObjId(id);
		history.setObjKey(CommonParameter.ID);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(3);
		history.setOperComment(msg);
		history.setReferUser(oldTestMission.getResponser());
		historyService.historyInsert(history);

		return testMissionDAO.testMissionDelete(id);
	}

	@Override
	public List<TestMission> notPlanedTMQuery(Map<String, Object> dataMap) {
		return testMissionDAO.notPlanedTMQuery(dataMap);
	}

	@Override
	public List<TestMission> planTMSQuery(int planId) {
		return testMissionDAO.planTMSQuery(planId);
	}

	@Override
	public int clearTestPlanMission(int planId) {
		return testMissionDAO.clearTestPlanMission(planId);
	}

}
