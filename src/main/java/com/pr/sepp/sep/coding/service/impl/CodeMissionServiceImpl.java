package com.pr.sepp.sep.coding.service.impl;

import com.pr.sepp.base.dao.BaseQueryDAO;
import com.pr.sepp.base.model.CodeMissionStatus;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.notify.model.Message;
import com.pr.sepp.notify.service.MessageService;
import com.pr.sepp.sep.coding.dao.CodeMissionDAO;
import com.pr.sepp.sep.coding.model.CodeMission;
import com.pr.sepp.sep.coding.service.CodeMissionService;
import com.pr.sepp.sep.requirement.dao.RequirementDAO;
import com.pr.sepp.sep.requirement.model.ReqStatusUpdate;
import com.pr.sepp.sep.requirement.model.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Transactional
public class CodeMissionServiceImpl implements CodeMissionService {

	@Autowired
	private HistoryService historyService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private CodeMissionDAO codeMissionDAO;

	@Autowired
	private RequirementDAO requirementDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private BaseQueryDAO baseQueryDAO;

	@Override
	public int cmsCreate(CodeMission codeMission) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int reqId = codeMission.getReqId();

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(CommonParameter.ID, reqId);
		Requirement oldReq = requirementDAO.reqQuery(queryMap).get(0);
		String reqSum = "【#" + reqId + " - " + oldReq.getSummary() + "】";

		codeMissionDAO.cmsCreate(codeMission);

		int createdId = codeMission.getId();

		reqStatusSynchronize(createdId, 1);

		Map<String, Object> operMap = new HashMap<>();
		operMap.put(CommonParameter.USER_ID, userId);
		operMap.put(CommonParameter.PRODUCT_ID, productId);
		String operName = userDAO.userQuery(operMap).get(0).getUserName();

		String msg = "用户【" + operName + "】在产品需求" + reqSum + "下拆分出新的开发任务：【#" + createdId + " - " + codeMission.getSummary() + "】";
		SEPPHistory history = new SEPPHistory();
		history.setObjType(6);
		history.setObjId(createdId);
		history.setObjKey(CommonParameter.ID);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(1);
		history.setNewValue(codeMission.toString());
		history.setOperComment(msg);
		history.setReferUser(codeMission.getResponser());
		historyService.historyInsert(history);

		List<Integer> messageTo = new ArrayList<>();
		messageTo.add(codeMission.getResponser());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(6);
		message.setObjectId(createdId);
		message.setTitle("您的新开发任务");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageTo);

		return createdId;
	}

	@Override
	public int cmsUpdate(CodeMission codeMission) throws IllegalAccessException {

		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int status = codeMission.getStatus();

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(CommonParameter.ID, codeMission.getId());
		queryMap.put(CommonParameter.PRODUCT_ID, productId);
		CodeMission oldCms = codeMissionDAO.cmsQuery(queryMap).get(0);

		Map<String, Object> operMap = new HashMap<>();
		operMap.put(CommonParameter.USER_ID, userId);
		operMap.put(CommonParameter.PRODUCT_ID, productId);
		String operName = userDAO.userQuery(operMap).get(0).getUserName();
		String msg = "开发任务：【#" + codeMission.getId() + " - " + codeMission.getSummary() + "】信息由用户【" + operName + "】操作更新";
		String suffix = "";

		if (status - oldCms.getStatus() != 0) {
			List<CodeMissionStatus> sts = baseQueryDAO.codeMissionStatus();
			String oldStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), oldCms.getStatus())).findFirst().orElse(new CodeMissionStatus()).getStatusName();
			String newStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), status)).findFirst().orElse(new CodeMissionStatus()).getStatusName();

			suffix = "，状态由【" + oldStatusName + "】改为【" + newStatusName + "】";

			reqStatusSynchronize(codeMission.getId(), status);
		}

		List<Integer> messageToSub = new ArrayList<>();
		messageToSub.add(oldCms.getSpliter());
		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(6);
		message.setObjectId(codeMission.getId());
		message.setTitle("开发任务更新提示");
		message.setContent("您拆分的" + msg + suffix);
		messageService.businessMessageGenerator(message, userId, messageToSub);

		if (oldCms.getResponser() - codeMission.getResponser() == 0) {    //开发负责人不发生变化的情况
			if (oldCms.getResponser() != userId && oldCms.getResponser() != oldCms.getSpliter()) { //非开发任务负责人本人操作
				List<Integer> messageToRes = new ArrayList<>();
				messageToRes.add(oldCms.getResponser());
				message.setContent(suffix + msg);
				messageService.businessMessageGenerator(message, userId, messageToRes);
			}
		} else { //变更了开发负责人的情况
			List<Integer> messageToResNew = new ArrayList<>();
			messageToResNew.add(codeMission.getResponser());
			String suffixNew = suffix + "，开发工作由【" + oldCms.getResponserName() + "】负责变为由您来负责";
			message.setContent(msg + suffixNew);
			messageService.businessMessageGenerator(message, userId, messageToResNew);

			List<Integer> messageToResOld = new ArrayList<>();
			messageToResOld.add(oldCms.getResponser());
			String suffixOld = suffix + "，并且转交给他人负责完成开发";
			message.setContent(msg + suffixOld);
			messageService.businessMessageGenerator(message, userId, messageToResOld);
		}

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends CodeMission> cls = codeMission.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(codeMission);
			Object oldValue = field.get(oldCms);

			if (keyName.endsWith("Name") || keyName.equals("reqDesc")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(6);
				history.setObjId(codeMission.getId());
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment(msg);
				history.setReferUser(codeMission.getResponser());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}
		return codeMissionDAO.cmsUpdate(codeMission);
	}

	@Override
	public List<CodeMission> cmsQuery(Map<String, Object> dataMap) {
		return codeMissionDAO.cmsQuery(dataMap);
	}

	@Override
	public int cmsStatusUpdate(int status, int id) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, id);
		CodeMission cms = codeMissionDAO.cmsQuery(dataMap).get(0);

		Map<String, Object> operMap = new HashMap<>();
		operMap.put(CommonParameter.USER_ID, userId);
		operMap.put(CommonParameter.PRODUCT_ID, productId);
		String operName = userDAO.userQuery(operMap).get(0).getUserName();

		dataMap.put(CommonParameter.STATUS, status);
		List<CodeMissionStatus> sts = baseQueryDAO.codeMissionStatus();
		String newStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), status)).findFirst().orElse(new CodeMissionStatus()).getStatusName();

		String msg = "开发任务：【#" + id + " - " + cms.getSummary() + "】由用户【"
				+ operName + "】操作，状态从【" + cms.getStatusName() + "】变为【" + newStatusName + "】";
		SEPPHistory history = new SEPPHistory();
		history.setObjType(6);
		history.setObjId(id);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(2);
		history.setOperComment(msg);
		history.setReferUser(cms.getResponser());
		history.setOrgValue(String.valueOf(cms.getStatus()));
		history.setNewValue(String.valueOf(status));
		history.setObjKey(CommonParameter.STATUS);
		historyService.historyInsert(history);

		List<Integer> messageToSub = new ArrayList<>();
		messageToSub.add(cms.getSpliter());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(6);
		message.setObjectId(id);
		message.setTitle("开发任务状态更新提示");
		message.setContent("您拆分的" + msg);
		messageService.businessMessageGenerator(message, userId, messageToSub);

		if (cms.getResponser() - cms.getSpliter() != 0) { //非开发任务负责人本人操作
			List<Integer> messageToRes = new ArrayList<>();
			messageToRes.add(cms.getResponser());
			message.setContent("您负责的" + msg);
			messageService.businessMessageGenerator(message, userId, messageToRes);
		}

		reqStatusSynchronize(id, status);

		return codeMissionDAO.cmsStatusUpdate(status, id);
	}

	private int cmsMap2Req(int cmNewStatus) {
		int mappedStatus = cmNewStatus;
		if (cmNewStatus == 1) {
			mappedStatus = 3;
		} else if (cmNewStatus == 2 || cmNewStatus == 3) {
			mappedStatus = 4;
		} else if (cmNewStatus == 4) {
			mappedStatus = 5;
		} else if (cmNewStatus == 5) {
			mappedStatus = 6;
		}
		return mappedStatus;
	}

	private int reqStatusSynchronize(int cmId, int cmNewStatus) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("cmId", cmId);
		Requirement requirement = requirementDAO.reqQuery(dataMap).get(0);

		int reqId = requirement.getId();
		int oldStatus = requirement.getStatus();

		Map<String, Object> cmsQueryMap = new HashMap<>();
		cmsQueryMap.put(CommonParameter.REQ_ID, reqId);
		cmsQueryMap.put(CommonParameter.PRODUCT_ID, productId);
		List<CodeMission> codeMissions = codeMissionDAO.cmsQuery(cmsQueryMap);

		int mappedStatus = cmsMap2Req(cmNewStatus);

		int newStatus;
		if (codeMissions.size() == 1) {
			newStatus = mappedStatus;
		} else {
			List<Integer> cmsStatusList = new ArrayList<>();
			for (int i = 0; i < codeMissions.size(); i++) {
				if (codeMissions.get(i).getId() == cmId) {
					cmsStatusList.add(cmNewStatus);
				} else {
					cmsStatusList.add(codeMissions.get(i).getStatus());
				}
			}
			Collections.sort(cmsStatusList);
			Set<Integer> cmsStatusSet = new HashSet<>(cmsStatusList);
			cmsStatusList = new ArrayList<>(cmsStatusSet);
			if (cmsStatusList.size() == 1) {
				newStatus = cmsMap2Req(cmsStatusList.get(0));
			} else {
				if (cmsStatusList.get(0) == 0) {
					newStatus = cmsMap2Req(cmsStatusList.get(1));
				} else {
					newStatus = cmsMap2Req(cmsStatusList.get(0));
				}
			}
		}
		if (newStatus == 0) {
			return 0;
		}

		if (oldStatus == newStatus) {
			return 0;
		}

		SEPPHistory history = new SEPPHistory();
		history.setObjType(2);
		history.setObjId(requirement.getId());
		history.setObjKey(CommonParameter.STATUS);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(2);
		history.setOrgValue(requirement.getStatus() + "");
		history.setNewValue(newStatus + "");
		history.setOperComment("产品需求状态随开发任务状态同步变更");
		history.setReferUser(requirement.getSubmitter());
		historyService.historyInsert(history);

		ReqStatusUpdate reqStatusUpdate = new ReqStatusUpdate();
		reqStatusUpdate.setId(reqId);
		reqStatusUpdate.setStatus(newStatus);

		return requirementDAO.reqStatusUpdate(reqStatusUpdate);
	}

	@Override
	public int reqCmsStatusSync(Integer reqId, Integer status) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		int res = codeMissionDAO.reqCmsStatusSync(reqId, status);

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("product_id", productId);
		dataMap.put("rel_id", reqId);
		List<CodeMission> cmss = codeMissionDAO.cmsQuery(dataMap);

		cmss.forEach(cms -> {
			String msg = "开发任务：【#" + cms.getId() + " - " + cms.getSummary() + "】已随产品需求同步关闭";
			SEPPHistory history = new SEPPHistory();
			history.setObjType(6);
			history.setObjId(cms.getId());
			history.setProductId(productId);
			history.setOperUser(userId);
			history.setOperType(2);
			history.setOperComment(msg);
			history.setReferUser(cms.getResponser());
			history.setObjKey(CommonParameter.STATUS);
			history.setOrgValue(String.valueOf(cms.getStatus()));
			history.setNewValue(String.valueOf(status));
			historyService.historyInsert(history);

			List<Integer> messageTo = new ArrayList<>();
			messageTo.add(cms.getSpliter());
			messageTo.add(cms.getResponser());

			Message message = new Message();
			message.setProductId(productId);
			message.setObjectType(6);
			message.setObjectId(cms.getId());
			message.setTitle("开发任务随产品需求同步关闭");
			message.setContent(msg);
			messageService.businessMessageGenerator(message, userId, messageTo);
		});

		return res;
	}

}
