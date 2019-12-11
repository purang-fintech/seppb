package com.pr.sepp.common.config;

import com.pr.sepp.common.websocket.handler.DeploymentBuildWebSocketHandler;
import com.pr.sepp.common.websocket.handler.MessageWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MessageWebSocketHandler messageWebSocketHandler;
    @Autowired
    private DeploymentBuildWebSocketHandler deploymentBuildWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageWebSocketHandler, "/myHandler")
                .addHandler(deploymentBuildWebSocketHandler, "/deployment-build")
                .addInterceptors(new HttpSessionHandshakeInterceptor()).setAllowedOrigins("*");
    }

}