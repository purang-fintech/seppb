package com.pr.sepp.common.websocket.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.GlobalCache;
import com.pr.sepp.common.websocket.GlobalSession;
import com.pr.sepp.common.websocket.model.MessageType;
import com.pr.sepp.notify.fetch.FetchClient;
import com.pr.sepp.notify.message.service.MessageService;
import com.pr.sepp.notify.model.GlobalDataResp;
import com.pr.sepp.notify.warning.service.WarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.pr.sepp.common.GlobalCache.buildWarningsCountMap;
import static com.pr.sepp.common.GlobalCache.getWarningCountLast;
import static com.pr.sepp.common.constants.CommonParameter.USER_ID;
import static com.pr.sepp.common.websocket.GlobalSession.attributesFetch;
import static com.pr.sepp.common.websocket.model.MessageType.MESSAGE_TYPE;

@Slf4j
@Component
public class MessageServer implements WebSocketServer<String, String> {

	@Autowired
	private FetchClient fetchClient;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private MessageService messageService;

	@Autowired
	private WarningService warningService;

	/**
	 * 每天零点清除缓存，以便于当天告警的计算
	 */
	@Scheduled(cron = "0 0 0 * * ?")
	public void clear() {
		GlobalCache.clear();
		log.info("缓存清除成功");
	}

	/**
	 * 默认将内存中维护的websession都推送到客户端
	 */
	@Override
	@Scheduled(fixedRate = 30 * 1000)
	public void pushAll() {
		Set<String> users = GlobalCache.getUserSessionMap().keySet();
		users.forEach(this::pushByT);
	}

	/**
	 * 为每个用户推送
	 *
	 * @param userId
	 */
	@Override
	public void pushByT(String userId) {
		Set<WebSocketSession> sessions = GlobalCache.getUserSessionMap().get(userId);
		sessions.forEach(session -> pushBySession(session, userId));
		messageService.updateSendStatusByUser(Integer.parseInt(userId), 1);

		// 告警消息一直保持，不消除
		// warningService.updateWarningMessageSendStatus(Integer.parseInt(userId), 1);
	}

	/**
	 * 理论上一个用户能打开无数个websession，然后为每个session推送相关的内容
	 *
	 * @param session
	 */
	@Override
	public void pushBySession(WebSocketSession session, String userId) {
		try {
			String message = mapper.writeValueAsString(responseBuilder(session));
			GlobalSession.sendMessage(session, message);
		} catch (Exception e) {
			log.error("websocket发送消息失败", e);
			GlobalSession.removeSession(Integer.valueOf(userId), session);
		}
	}

	public GlobalDataResp responseBuilder(WebSocketSession session) {
		Optional<List<MessageType>> optionalMessageTypes = attributesFetch(session, MESSAGE_TYPE);
		return optionalMessageTypes.map(messageTypes -> responseBuilder(session, messageTypes))
				.orElseGet(GlobalDataResp::new);
	}

	private GlobalDataResp responseBuilder(WebSocketSession session, List<MessageType> messageTypes) {
		GlobalDataResp globalDataResp = new GlobalDataResp();
		for (MessageType messageType : messageTypes) {
			PageInfo info = fetchClient.notifyMessage(session, messageType);
			globalDataResp.apply(info, messageType);
		}
		long total = globalDataResp.getWarnings().getTotal();
		Optional<Integer> optionalUserId = attributesFetch(session, USER_ID);
		optionalUserId.ifPresent(uid -> {
			globalDataResp.setNewWarningCount(total - getWarningCountLast(uid));
			buildWarningsCountMap(uid, total);
		});
		return globalDataResp;
	}

	@PreDestroy
	public void destroy() {
		GlobalCache.clear();
	}
}
