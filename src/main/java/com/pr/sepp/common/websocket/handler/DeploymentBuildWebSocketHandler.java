package com.pr.sepp.common.websocket.handler;

import com.pr.sepp.common.websocket.GlobalSession;
import com.pr.sepp.common.websocket.model.DeploymentWebSessionPayload;
import com.pr.sepp.common.websocket.push.DeploymentBuildServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

import static com.pr.sepp.common.constants.CommonParameter.JOB_NAME;
import static com.pr.sepp.common.websocket.GlobalSession.setSessionForJob;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class DeploymentBuildWebSocketHandler extends TextWebSocketHandler {

    private static final String HEART_BEAT = "HeartBeat";

    @Autowired
    private DeploymentBuildServer deploymentBuildServer;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        if (isNotBlank(message.getPayload()) && heartBeat(message.getPayload())) {
            try {
                DeploymentWebSessionPayload payload = DeploymentWebSessionPayload.apply(message);
                session.getAttributes().put(JOB_NAME, payload);
                setSessionForJob(payload, session);
                deploymentBuildServer.pushBySession(session, payload);
            } catch (IOException e) {
                log.error("消息序列号失败：{}", e);
            }
        }
    }

    /**
     * session关闭后清除内存中的相关session
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        DeploymentWebSessionPayload payload = (DeploymentWebSessionPayload) attributes.get(JOB_NAME);
        GlobalSession.removeSessionByJob(payload, session);
    }

    private boolean heartBeat(String payload) {
        return !HEART_BEAT.equalsIgnoreCase(payload);
    }
}
