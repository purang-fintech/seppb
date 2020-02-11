package com.pr.sepp.sep.defect.service.impl;

import com.pr.sepp.base.dao.BaseQueryDAO;
import com.pr.sepp.base.model.DefectStatus;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.notify.message.model.Message;
import com.pr.sepp.notify.message.service.MessageService;
import com.pr.sepp.sep.defect.dao.DefectDAO;
import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.sep.defect.service.DefectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Transactional
@Service("defectService")
public class DefectServiceImpl implements DefectService {

	@Autowired
	private DefectDAO defectDAO;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private MessageService messageService;

	@Autowired
	BaseQueryDAO baseQueryDAO;

	@Autowired
	UserDAO userDAO;

	@Override
	public List<Defect> defectQuery(Map<String, Object> dataMap) {
		return defectDAO.defectQuery(dataMap);
	}

	@Override
	public int defectInfoCreate(Defect defect) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		defect.setSubmitter(userId);
		defect.setProductId(productId);
		defect.setFixTimes(0);

		defectDAO.defectInfoCreate(defect);

		String msg = "提交新的缺陷：【#" + defect.getId() + " - " + defect.getSummary() + "】";

		SEPPHistory history = new SEPPHistory();
		history.setObjType(5);
		history.setObjId(defect.getId());
		history.setObjKey(CommonParameter.ID);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(1);
		history.setNewValue(defect.toString());
		history.setOperComment(msg);
		history.setReferUser(defect.getConciliator());
		historyService.historyInsert(history);

		Map<String, Object> subMap = new HashMap<>();
		subMap.put(CommonParameter.USER_ID, userId);
		subMap.put(CommonParameter.PRODUCT_ID, productId);
		String submitterName = userDAO.userQuery(subMap).get(0).getUserName();
		String prefix = "用户【" + submitterName + "】已";
		boolean responsed = !Objects.isNull(defect.getResponser());

		List<Integer> messageTo = new ArrayList<>();
		messageTo.add(defect.getConciliator());
		String suffix = "，已经指定缺陷协调人为您本人" + (responsed ? "" : "，修复负责人尚未制定，请尽快确认并且分配");

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(5);
		message.setObjectId(defect.getId());
		message.setTitle("提交新的缺陷");
		message.setContent(prefix + msg + suffix);
		messageService.businessMessageGenerator(message, userId, messageTo);

		if (responsed) {
			List<Integer> messageToRes = new ArrayList<>();
			messageToRes.add(defect.getResponser());
			suffix = "，修复工作被指定为由您来负责跟进";

			message.setContent(prefix + msg + suffix);
			messageService.businessMessageGenerator(message, userId, messageToRes);
		}

		return defect.getId();
	}

	@Override
	public int defectInfoUpdate(Defect defect) throws IllegalAccessException {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int statusInt = defect.getStatus();
		int id = defect.getId();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, id);
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		Defect oldDefect = defectDAO.defectQuery(dataMap).get(0);
		List<DefectStatus> sts = baseQueryDAO.defectStatus();
		String oldStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), oldDefect.getStatus())).findFirst().orElse(new DefectStatus()).getStatusName();
		String newStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), statusInt)).findFirst().orElse(new DefectStatus()).getStatusName();

		// 原状态为拒绝，新状态为非拒绝且非关闭时，需要清空拒绝操作信息
		if (statusInt != 6 && statusInt != 0 && oldDefect.getStatus() == 6) {
			defect.setRefuseReason(null);
			defect.setRefuseDetail(null);
			defect.setSameCodeDefect(null);
		}

		int fixTimes = oldDefect.getFixTimes(); // closed then fixTimes + 1
		if ((oldDefect.getStatus() == 5 || oldDefect.getStatus() == 4) && (statusInt == 1 || statusInt == 2)) {
			defect.setFixTimes(fixTimes + 1);
		} else if (statusInt == 0 && oldDefect.getStatus() != 0) {
			defect.setFixTimes(fixTimes + 1);
		} else {
			defect.setFixTimes(fixTimes);
		}
		defect.setProductId(oldDefect.getProductId());
		defect.setSubmitter(oldDefect.getSubmitter());
		defect.setFoundTime(oldDefect.getFoundTime());

		String msg = "缺陷：【#" + defect.getId() + " - " + defect.getSummary() + "】";
		Map<String, Object> operMap = new HashMap<>();
		operMap.put(CommonParameter.USER_ID, userId);
		operMap.put(CommonParameter.PRODUCT_ID, productId);
		String operName = userDAO.userQuery(operMap).get(0).getUserName();
		String suffix = "信息被用户【" + operName + "】更新";

		if (statusInt - oldDefect.getStatus() != 0) {
			suffix = "，被用户【" + operName + "】将状态由【" + oldStatusName + "】改为【" + newStatusName + "】";
		}

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(5);
		message.setObjectId(defect.getId());
		message.setTitle("缺陷信息更新提示");

		if (defect.getConciliator() - oldDefect.getConciliator() != 0) {
			if (userId - defect.getConciliator() != 0) {        //通知新负责人
				String suffix_1 = suffix;
				suffix_1 += "，缺陷协调人由【" + oldDefect.getConciliatorName() + "】变为您";
				List<Integer> messageToConNew = new ArrayList<>();
				messageToConNew.add(defect.getConciliator());
				message.setContent(msg + suffix_1);
				messageService.businessMessageGenerator(message, userId, messageToConNew);
			}
			if (userId - oldDefect.getConciliator() != 0) {    //通知原负责人
				suffix += "，缺陷协调人变更为他人";
				List<Integer> messageToConOld = new ArrayList<>();
				messageToConOld.add(oldDefect.getConciliator());
				message.setContent(msg + suffix);
				messageService.businessMessageGenerator(message, userId, messageToConOld);
			}
		}

		if (null != defect.getResponser()) {        //前端确保缺陷负责人一旦有值就必须持续非空，否则此处还需再加判断
			if (null == oldDefect.getResponser() || defect.getResponser() - oldDefect.getResponser() != 0) { //修复责任人变更，原负责人和新负责人都需要收到通知
				if (userId - defect.getResponser() != 0) {        //通知新负责人
					String suffix_1 = suffix;
					suffix_1 += null == oldDefect.getResponser() ? "，修复工作变为您来负责跟进" : "，修复工作由【" + oldDefect.getResponserName() + "】变为您来负责跟进";
					List<Integer> messageToResNew = new ArrayList<>();
					messageToResNew.add(defect.getResponser());
					message.setContent(msg + suffix_1);
					messageService.businessMessageGenerator(message, userId, messageToResNew);
				}
				if (null != oldDefect.getResponser() && userId - oldDefect.getResponser() != 0) {    //通知原负责人
					suffix += "，并且转交给他人负责跟进";
					List<Integer> messageToResOld = new ArrayList<>();
					messageToResOld.add(oldDefect.getResponser());
					message.setContent(msg + suffix);
					messageService.businessMessageGenerator(message, userId, messageToResOld);
				}
			} else {    //修复责任人未发生变化，直接通知信息变更
				if (userId - oldDefect.getResponser() != 0) {    //通知原负责人
					List<Integer> messageToRes = new ArrayList<>();
					messageToRes.add(oldDefect.getResponser());
					message.setContent(msg + suffix);
					messageService.businessMessageGenerator(message, userId, messageToRes);
				}
			}
		}

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends Defect> cls = defect.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(defect);
			Object oldValue = field.get(oldDefect);

			if (keyName.endsWith("Name") || keyName.equals(CommonParameter.REL_CODE) || keyName.equals("productor")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(5);
				history.setObjId(oldDefect.getId());
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment(msg + "记录更新");
				history.setReferUser(defect.getResponser());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return defectDAO.defectInfoUpdate(defect);
	}

	@Override
	public int defectStatusUpdate(Defect defectRequestParam) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int status = defectRequestParam.getStatus();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, defectRequestParam.getId());
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		Defect oldDefect = defectDAO.defectQuery(dataMap).get(0);
		List<DefectStatus> sts = baseQueryDAO.defectStatus();
		String oldStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), oldDefect.getStatus())).findFirst().orElse(new DefectStatus()).getStatusName();
		String newStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), status)).findFirst().orElse(new DefectStatus()).getStatusName();
		String msg = "缺陷：【#" + oldDefect.getId() + " - " + oldDefect.getSummary() + "】信息更新，状态已由【" + oldStatusName + "】变为【" + newStatusName + "】";

		// 原状态为拒绝，新状态为非拒绝且非关闭时，需要清空拒绝操作信息
		if (status != 6 && status != 0 && oldDefect.getStatus() == 6) {
			defectRequestParam.setRefuseReason(null);
			defectRequestParam.setRefuseDetail(null);
			defectRequestParam.setSameCodeDefect(null);
		}

		List<Integer> messageTo = new ArrayList<>();
		messageTo.add(oldDefect.getSubmitter());
		if (status == 6) {
			messageTo.add(oldDefect.getConciliator());
		}

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(5);
		message.setObjectId(defectRequestParam.getId());
		message.setTitle("缺陷信息更新提示");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageTo);

		if (null != defectRequestParam.getResponser()) {
			String suffix = msg;
			suffix += "，修复工作交由您来负责跟进";
			List<Integer> messageToResNew = new ArrayList<>();
			messageToResNew.add(defectRequestParam.getResponser());
			message.setContent(suffix);
			messageService.businessMessageGenerator(message, userId, messageToResNew);
		}

		int fixTimes = oldDefect.getFixTimes(); // closed then fixTimes + 1
		if ((oldDefect.getStatus() == 5 || oldDefect.getStatus() == 4) && (status == 1 || status == 2)) {
			fixTimes += 1;
		} else if (status == 0 && oldDefect.getStatus() != 0) {
			fixTimes += 1;
		}
		defectRequestParam.setFixTimes(fixTimes);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(5);
		history.setObjId(oldDefect.getId());
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(2);
		history.setOperComment(msg);
		history.setReferUser(oldDefect.getResponser());
		history.setOrgValue(String.valueOf(oldDefect.getStatus()));
		history.setNewValue(String.valueOf(status));
		history.setObjKey(CommonParameter.STATUS);

		historyService.historyInsert(history);
		return defectDAO.defectStatusUpdate(defectRequestParam);
	}

	@Override
	public int defectQueryNC(Map<String, String> dataMap) {
		return defectDAO.defectQueryNC(dataMap);
	}

	@Override
	public List<Defect> refusedDefectQuery(Map<String, Object> dataMap) {
		return defectDAO.refusedDefectQuery(dataMap);
	}
}
