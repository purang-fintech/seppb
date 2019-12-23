package com.pr.sepp.notify.fetch;

import com.pr.sepp.notify.model.resp.MessageResp;
import com.pr.sepp.notify.service.MessageService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static com.pr.sepp.common.constants.CommonParameter.*;
import static com.pr.sepp.common.websocket.GlobalSession.attributesFetch;

@Component("noticeFetch")
public class NoticeInfoFetch implements BaseFetch {

    @Autowired
    private MessageService messageService;

    @Override
    public PageInfo<MessageResp> fetch(WebSocketSession session) {
        Optional<Integer> optionalUserId = attributesFetch(session, USER_ID);
        Optional<Integer> optionalProductId = attributesFetch(session, PRODUCT_ID);
        return optionalUserId.map(userId -> messageService.messageListPaging(userId, optionalProductId.get()))
                .orElseGet(PageInfo::new);
    }
}
