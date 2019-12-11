package com.pr.sepp.notify.fetch;

import com.pr.sepp.common.config.factory.CommonFactory;
import com.pr.sepp.common.websocket.model.MessageType;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class FetchClient {

    @Autowired
    private CommonFactory commonFactory;

    public PageInfo notifyMessage(WebSocketSession session,MessageType messageType) {
        BaseFetch baseFetch = commonFactory.getBean(messageType.getBeanName());
        return baseFetch.fetch(session);
    }
}
