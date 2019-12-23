package com.pr.sepp.common.websocket;

import com.google.common.collect.Sets;
import com.pr.sepp.common.websocket.model.DeploymentWebSessionPayload;
import com.pr.sepp.common.websocket.model.WebSessionPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.nullToEmpty;
import static com.pr.sepp.common.GlobalCache.jobSessionMap;
import static com.pr.sepp.common.GlobalCache.userSessionMap;
import static com.pr.sepp.common.constants.CommonParameter.*;
import static com.pr.sepp.common.websocket.model.MessageType.MESSAGE_TYPE;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Slf4j
public final class GlobalSession {
    private GlobalSession() {}

    public static WebSocketSession setSessionForUser(Integer userId, Integer productId, WebSocketSession webSession) {
        webSession.getAttributes().put(USER_ID, userId);
        webSession.getAttributes().put(PRODUCT_ID, productId);
        Set<WebSocketSession> webSocketSessions = userSessionMap.computeIfAbsent(String.valueOf(userId), a -> Sets.newConcurrentHashSet());
        webSocketSessions.add(webSession);
        return webSession;
    }

    public static WebSocketSession setSessionForJob(DeploymentWebSessionPayload payload, WebSocketSession webSession) {
        Set<WebSocketSession> webSocketSessions = jobSessionMap.computeIfAbsent(payload, a -> Sets.newConcurrentHashSet());
        webSocketSessions.add(webSession);
        return webSession;
    }

    /**
     * 当客户端与服务端的连接关闭时，需要清除内存中管理的session
     *
     * @param userId
     */
    public static void removeSession(Integer userId, WebSocketSession webSocketSession) {
        Set<WebSocketSession> webSocketSessions = userSessionMap.get(nullToEmpty(String.valueOf(userId)));
        if (isNotEmpty(webSocketSessions)) {
            webSocketSessions.remove(webSocketSession);
            if (equal(webSocketSessions.size(), 0)) {
                userSessionMap.remove(userId);
            }
            try {
                webSocketSession.close();
            } catch (IOException e) {
                log.error("用户{}的WebSocketSession关闭失败:{}", userId, e);
            }
        }
    }

    public static void removeSessionByJob(DeploymentWebSessionPayload payload, WebSocketSession session) {
        Set<WebSocketSession> webSocketSessions = jobSessionMap.get(payload);
        if (isNotEmpty(webSocketSessions)) {
            webSocketSessions.remove(session);
            if (equal(webSocketSessions.size(), 0)) {
                jobSessionMap.remove(payload);
            }
        }
    }

    /**
     * 封装每个session中需要管理的参数
     * 例如：websockets做分页时，需要pageNum.和pageSize
     *
     * @param webSessionPayload
     * @return
     */
    public static WebSocketSession buildWebSocketSession(WebSocketSession webSocketSession, WebSessionPayload webSessionPayload) {
        acquireUserIdAndSetSession(webSocketSession, webSessionPayload);
        Map<String, Object> attributes = webSocketSession.getAttributes();
        attributes.put(ALERT_PAGE_NUM, webSessionPayload.getAlertPage().getPageNum());
        attributes.put(ALERT_PAGE_SIZE, webSessionPayload.getAlertPage().getPageSize());
        attributes.put(MESSAGE_PAGE_NUM, webSessionPayload.getMessagePage().getPageNum());
        attributes.put(MESSAGE_PAGE_SIZE, webSessionPayload.getMessagePage().getPageSize());
        attributes.put(MESSAGE_TYPE, webSessionPayload.getMessageTypes());
        return webSocketSession;
    }

    private static void acquireUserIdAndSetSession(WebSocketSession webSocketSession, WebSessionPayload payload) {
        setSessionForUser(payload.getUserId(), payload.getProductId(), webSocketSession);
    }

    /**
     * 向客户端发送消息
     *
     * @param webSocketSession
     * @param message          (需要json格式的字符串，方便客户端处理)
     * @throws IOException
     */
    public static void sendMessage(WebSocketSession webSocketSession, String message) throws IOException {
        TextMessage textMessage = new TextMessage(message);
        if (webSocketSession.isOpen()) {
            webSocketSession.sendMessage(textMessage);
        }
    }

    public static <T> Optional<T> attributesFetch(WebSocketSession session, String key) {
        Map<String, Object> attributes = session.getAttributes();
        Object obj = attributes.get(key);
        if (obj == null) {
            return Optional.empty();
        }
        T t = (T) obj;
        return Optional.of(t);
    }


}
