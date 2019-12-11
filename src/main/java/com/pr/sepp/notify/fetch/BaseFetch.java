package com.pr.sepp.notify.fetch;

import com.github.pagehelper.PageInfo;
import org.springframework.web.socket.WebSocketSession;

public interface BaseFetch<T> {

    PageInfo<T> fetch(WebSocketSession session);

}
