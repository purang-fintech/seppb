package com.pr.sepp.common.websocket.handler;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.common.websocket.GlobalSession;
import com.pr.sepp.common.websocket.push.MessageServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Objects;

import static com.pr.sepp.common.GlobalCache.userSessionMap;
import static com.pr.sepp.common.constants.CommonParameter.USER_ID;
import static com.pr.sepp.common.websocket.GlobalSession.buildWebSocketSession;
import static com.pr.sepp.common.websocket.GlobalSession.setSessionForUser;
import static com.pr.sepp.common.websocket.model.WebSessionPayload.apply;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private MessageServer messageServer;
    private static final String HEART_BEAT = "HeartBeat";

    /**
     * 客户端与服务端建立连接后，将该连接与用户绑定
     *
     * @param session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (Objects.isNull(ParameterThreadLocal.getUserId())) {
            return;
        }
        if (nonNull(ParameterThreadLocal.getUserId()) && nonNull(ParameterThreadLocal.getProductId())) {
            setSessionForUser(ParameterThreadLocal.getUserId(), ParameterThreadLocal.getProductId(), session);
            log.info("当前用户连接数为:{}", userSessionMap.size());
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            if (heartBeat(message.getPayload()) && null != apply(message.getPayload()).getUserId()) {
                buildWebSocketSession(session, apply(message.getPayload()));
                messageServer.pushBySession(session, null);
            }
        } catch (Exception e) {
            log.error("消息推送错误:{}", e);
        }
    }

    /**
     * session关闭后清除内存中的相关session
     *
     * @param session
     * @param status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Map<String, Object> attributes = session.getAttributes();
        Object id = attributes.get(USER_ID);
        if (nonNull(id)) {
            Integer userId = (int) id;
            GlobalSession.removeSession(userId, session);
        }
    }

    private boolean heartBeat(String payload) {
        return !HEART_BEAT.equalsIgnoreCase(payload);
    }

}