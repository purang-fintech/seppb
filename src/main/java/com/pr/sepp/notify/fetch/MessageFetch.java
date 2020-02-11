package com.pr.sepp.notify.fetch;

import com.github.pagehelper.PageInfo;
import com.pr.sepp.notify.message.model.resp.MessageResp;
import com.pr.sepp.notify.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static com.pr.sepp.common.constants.CommonParameter.PRODUCT_ID;
import static com.pr.sepp.common.constants.CommonParameter.USER_ID;
import static com.pr.sepp.common.websocket.GlobalSession.attributesFetch;

@Component("messageFetch")
public class MessageFetch implements BaseFetch {

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
