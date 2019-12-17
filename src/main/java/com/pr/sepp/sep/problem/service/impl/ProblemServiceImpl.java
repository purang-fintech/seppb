package com.pr.sepp.sep.problem.service.impl;

import com.pr.sepp.base.model.ProblemStatus;
import com.pr.sepp.base.service.BaseQueryService;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.module.dao.ModuleDAO;
import com.pr.sepp.mgr.module.model.Module;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.notify.model.Message;
import com.pr.sepp.notify.service.MessageService;
import com.pr.sepp.sep.problem.dao.ProblemDAO;
import com.pr.sepp.sep.problem.model.Problem;
import com.pr.sepp.sep.problem.model.ProblemRefuse;
import com.pr.sepp.sep.problem.service.ProblemService;
import com.pr.sepp.sep.requirement.dao.RequirementDAO;
import com.pr.sepp.sep.requirement.model.ReqStatusUpdate;
import com.pr.sepp.sep.requirement.model.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Transactional
public class ProblemServiceImpl implements ProblemService {

	@Autowired
	private ProblemDAO problemDAO;

	@Autowired
	private RequirementDAO requirementDAO;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private MessageService messageService;

	@Autowired
	BaseQueryService baseQueryService;

	@Autowired
	private ModuleDAO moduleDAO;

	@Autowired
	private UserDAO userDAO;

	@Override
	public int problemCreate(Problem problem) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		problem.setProductId(productId);

		problemDAO.problemCreate(problem);
		int createdId = problem.getId();

		String msg = "提交新的线上问题：【#" + createdId + " - " + problem.getSummary() + "】";
		SEPPHistory history = new SEPPHistory();
		history.setObjType(17);
		history.setObjId(createdId);
		history.setObjKey(CommonParameter.ID);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(1);
		history.setNewValue(problem.toString());
		history.setOperComment(msg);
		historyService.historyInsert(history);

		List<Integer> messageTo = new ArrayList<>();

		// 通知对应模块的开发、测试、产品负责人以及项目经理
		Map<String, Object> moduleMap = new HashMap<>();
		moduleMap.put(CommonParameter.MODULE_ID, problem.getModuleId());
		Module module = moduleDAO.moduleQuery(moduleMap).get(0);
		messageTo.add(module.getPdResponser());
		messageTo.add(module.getDevResponser());
		messageTo.add(module.getTestResponser());
		// 项目经理
		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.PRODUCT_ID, productId);
		userMap.put("roleId", "10");
		List<User> pms = userDAO.userQueryProductRole(userMap);
		pms.forEach(user -> messageTo.add(user.getUserId()));

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(17);
		message.setObjectId(createdId);
		message.setTitle("提交新的线上问题");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageTo);

		return createdId;
	}

	@Override
	public int problemUpdate(Problem problem) throws IllegalAccessException {
		Integer userId = ParameterThreadLocal.getUserId();
		Integer productId = ParameterThreadLocal.getProductId();
		int id = problem.getId();

		Map<String, Object> queryOld = new HashMap<>();
		queryOld.put(CommonParameter.ID, id);
		queryOld.put(CommonParameter.PRODUCT_ID, productId);
		Problem oldProblem = problemDAO.problemQuery(queryOld).get(0);

		List<Integer> messageTo = new ArrayList<>();
		messageTo.add(oldProblem.getSubmitter());
		messageTo.add(problem.getResponser());

		String msg = "线上问题：【#" + id + " - " + problem.getSummary() + "】信息已更新";
		if (oldProblem.getStatus() - problem.getStatus() != 0) {
			List<ProblemStatus> sts = baseQueryService.problemStatus();
			String oldStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), oldProblem.getStatus())).findFirst().orElse(new ProblemStatus()).getStatusName();
			String newStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), problem.getStatus())).findFirst().orElse(new ProblemStatus()).getStatusName();

			msg += "，状态由【" + oldStatusName + "】改为【" + newStatusName + "】";
		}

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(17);
		message.setObjectId(id);
		message.setTitle("线上问题修改提示");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageTo);

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends Problem> cls = problem.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(problem);
			Object oldValue = field.get(oldProblem);

			if (keyName.endsWith("Name")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(17);
				history.setObjId(problem.getId());
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment(msg);
				history.setReferUser(oldProblem.getSubmitter());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return problemDAO.problemUpdate(problem);
	}

	@Override
	public List<Problem> problemQuery(Map<String, Object> dataMap) {
		return problemDAO.problemQuery(dataMap);
	}

	@Override
	public int problemRefuse(ProblemRefuse problemRefuse) {
		Integer userId = ParameterThreadLocal.getUserId();
		Integer productId = ParameterThreadLocal.getProductId();
		Integer id = problemRefuse.getId();

		Map<String, Object> queryOld = new HashMap<>();
		queryOld.put(CommonParameter.ID, id);
		queryOld.put(CommonParameter.PRODUCT_ID, productId);
		Problem oldProblem = problemDAO.problemQuery(queryOld).get(0);

		if (null != oldProblem.getTransId()) {
			ReqStatusUpdate reqStatusUpdate = new ReqStatusUpdate();
			reqStatusUpdate.setId(oldProblem.getTransId());
			reqStatusUpdate.setStatus(0);
			reqStatusUpdate.setCloseStyle(2);
			requirementDAO.reqStatusUpdate(reqStatusUpdate);
		}

		String msg = "线上问题：【#" + id + " - " + oldProblem.getSummary() + "】被拒绝";
		SEPPHistory history = new SEPPHistory();
		history.setObjType(17);
		history.setObjId(id);
		history.setObjKey(CommonParameter.STATUS);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(2);
		history.setOrgValue(String.valueOf(oldProblem.getStatus()));
		history.setNewValue("0");
		history.setOperComment(msg);
		historyService.historyInsert(history);

		List<Integer> messageTo = new ArrayList<>();
		messageTo.add(oldProblem.getSubmitter());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(17);
		message.setObjectId(id);
		message.setTitle("线上问题被拒绝");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageTo);

		return problemDAO.problemRefuse(problemRefuse);
	}

}
