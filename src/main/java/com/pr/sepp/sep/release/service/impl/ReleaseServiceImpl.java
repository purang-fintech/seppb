package com.pr.sepp.sep.release.service.impl;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.notify.model.Message;
import com.pr.sepp.notify.service.MessageService;
import com.pr.sepp.sep.release.dao.ReleaseDAO;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.release.service.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Transactional
public class ReleaseServiceImpl implements ReleaseService {

	@Autowired
	private HistoryService historyService;

	@Autowired
	private MessageService messageService;

	@Autowired
	ReleaseDAO releaseDAO;

	@Autowired
	private UserDAO userDAO;

	@Override
	public List<Release> releaseQuery(Map<String, Object> dataMap) {
		return releaseDAO.releaseQuery(dataMap);
	}

	@Override
	public int releaseCreate(Release release) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		release.setProductId(productId);
		releaseDAO.releaseCreate(release);
		int createdId = release.getId();

		String msgRel = "创建版本计划【" + release.getRelCode() + "】，发布日期为【" + release.getRelDate() + "】";
		SEPPHistory history = new SEPPHistory();
		history.setObjType(4);
		history.setObjId(createdId);
		history.setObjKey(CommonParameter.ID);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(1);
		history.setNewValue(String.valueOf(createdId));
		history.setOperComment(msgRel);
		history.setReferUser(release.getResponser());
		historyService.historyInsert(history);

		// 所有本产品项目经理、版本经理和版本负责人需要收到消息提醒
		List<Integer> messageTo = new ArrayList<>();
		messageTo.add(release.getResponser());

		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.PRODUCT_ID, productId);
		userMap.put("roleId", 10);
		List<User> pms = userDAO.userQueryProductRole(userMap);
		pms.forEach(user -> {
			if (Objects.equals(user.getUserId(), release.getResponser())) {
				return;
			}
			messageTo.add(user.getUserId());
		});

		userMap.put("roleId", 2);
		List<User> rms = userDAO.userQueryProductRole(userMap);
		rms.forEach(user -> {
			if (Objects.equals(user.getUserId(), release.getResponser())) {
				return;
			}
			messageTo.add(user.getUserId());
		});

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(4);
		message.setObjectId(createdId);
		message.setTitle("新建版本/发布计划提示");
		message.setContent(msgRel);
		messageService.businessMessageGenerator(message, userId, messageTo);

		return createdId;
	}

	@Override
	public int releaseUpdate(Release release) throws IllegalAccessException {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(CommonParameter.ID, release.getId());
		Release relOld = releaseDAO.releaseQuery(queryMap).get(0);

		release.setRelCode(relOld.getRelCode());
		release.setProductId(relOld.getProductId());
		release.setCreator(relOld.getCreator());
		release.setCreateDate(relOld.getCreateDate());

		String msgRel = "版本计划【" + relOld.getRelCode() + "】发生变更";

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends Release> cls = release.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(release);
			Object oldValue = field.get(relOld);

			if (keyName.endsWith("Name")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(4);
				history.setObjId(release.getId());
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment(msgRel);
				history.setReferUser(release.getResponser());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		List<Integer> messageTo = new ArrayList<>();
		messageTo.add(release.getResponser());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(4);
		message.setObjectId(release.getId());
		message.setTitle(msgRel);
		message.setContent(msgRel + "，请确认详情！");
		messageService.businessMessageGenerator(message, userId, messageTo);

		return releaseDAO.releaseUpdate(release);
	}

	@Override
	public List<Release> openReleaseQuery(Map<String, String> dataMap) {
		return releaseDAO.openReleaseQuery(dataMap);
	}

}
