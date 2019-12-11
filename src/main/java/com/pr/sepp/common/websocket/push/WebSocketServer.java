package com.pr.sepp.common.websocket.push;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketServer<T,E> {

    void pushAll();

    /**
     * 参数为Map的key
     */
    void pushByT(T t);

    /**
     * 为每个session推送相关的内容
     *
     * @param session
     */
    void pushBySession(WebSocketSession session,E e);
}
