package com.pr.sepp.notify.service;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.common.websocket.push.MessageServer;
import com.pr.sepp.mgr.user.dao.UserSettingDAO;
import com.pr.sepp.mgr.user.model.UserSetting;
import com.pr.sepp.notify.dao.MessageDAO;
import com.pr.sepp.notify.model.Message;
import com.pr.sepp.notify.model.MessageFlow;
import com.pr.sepp.notify.model.req.HaveReadMessageReq;
import com.pr.sepp.notify.model.resp.MessageResp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class MessageService {
	@Autowired
	private MessageDAO messageDAO;

	@Autowired
	private MessageServer messageServer;

	@Autowired
	private UserSettingDAO userSettingDAO;

	public PageInfo<MessageResp> messageListPaging(Integer userId, Integer productId) {
		List<MessageResp> messages = messageDAO.findMessagesByUser(HaveReadMessageReq.builder().userId(userId).productId(productId).isRead(false).build());
		return new PageInfo<>(messages);
	}

	@Transactional(rollbackFor = Exception.class)
	public void messageHaveRead(List<Long> ids) {
		messageDAO.updateMessageToHaveRead(ids);
		messageServer.pushByT(String.valueOf(ParameterThreadLocal.getUserId()));
	}

	@Transactional(rollbackFor = Exception.class)
	public void messageAllHaveReadByUser(Integer userId) {
		messageDAO.UpdateAllMessageToHaveReadByUser(userId);
		messageServer.pushByT(String.valueOf(ParameterThreadLocal.getUserId()));
	}

	public synchronized void updateSendStatusByUser(Integer userId, Integer messageType) {
		messageDAO.updateMessageDeliveryStatus(userId, messageType);
	}

	public synchronized void businessMessageGenerator(Message message, Integer userId, List<Integer> messageTo) {
		messageDAO.createMessage(message);
		long messageId = message.getId();

		List<MessageFlow> messageFlows = new ArrayList<>();

		messageTo.forEach(d -> {
			UserSetting setting = userSettingDAO.userSettingQuery(d);
			List<String> emailSubscribe = new ArrayList<>();
			List<String> messageSubscribe = new ArrayList<>();
			if (null == setting) {
				log.error("用户订阅设置查无结果！");
				return;
			}
			if (setting.getMessageOn() != 1) {
				log.warn("用户#" + d + "未打开系统通知，不接受任何信息！");
				return;
			}
			if (StringUtils.isEmpty(setting.getEmailSubscribe())) {
				log.warn("用户#" + d + "邮件订阅设置查无结果！");
			} else {
				emailSubscribe = Arrays.asList(setting.getEmailSubscribe().split(","));
				emailSubscribe = emailSubscribe.stream().filter(f -> Integer.parseInt(f) - message.getObjectType() == 0).collect(toList());
			}
			if (StringUtils.isEmpty(setting.getMessageSubscribe())) {
				log.warn("用户#" + d + "消息订阅设置查无结果！");
			} else {
				messageSubscribe = Arrays.asList(setting.getMessageSubscribe().split(","));
				messageSubscribe = messageSubscribe.stream().filter(f -> Integer.parseInt(f) - message.getObjectType() == 0).collect(toList());
			}

			if (messageSubscribe.size() > 0) {
				MessageFlow messageFlow = new MessageFlow();
				messageFlow.setMessageId(messageId);
				messageFlow.setIsRead(0);
				messageFlow.setCreator(userId);
				messageFlow.setType(1);
				messageFlow.setUserId(d);
				messageFlows.add(messageFlow);
			}

			if (emailSubscribe.size() > 0) {
				MessageFlow messageFlow = new MessageFlow();
				messageFlow.setMessageId(messageId);
				messageFlow.setIsRead(0);
				messageFlow.setCreator(userId);
				messageFlow.setType(2);
				messageFlow.setUserId(d);
				messageFlows.add(messageFlow);
			}
		});

		if (messageFlows.size() > 0) {
			messageDAO.batchCreateMessageFlow(messageFlows);
		}
	}

	public PageInfo<MessageResp> readMessagesByUser(HaveReadMessageReq haveReadMessageReq) {
		PageHelper.startPage(haveReadMessageReq.getPageNum(), haveReadMessageReq.getPageSize());
		haveReadMessageReq.setUserId(ParameterThreadLocal.getUserId());
		haveReadMessageReq.setProductId(ParameterThreadLocal.getProductId());
		haveReadMessageReq.setRead(true);
		List<MessageResp> messageResps = messageDAO.findMessagesByUser(haveReadMessageReq);
		return new PageInfo<>(messageResps);
	}
}