package com.pr.sepp.notify.fetch;

import com.github.pagehelper.PageInfo;
import com.pr.sepp.notify.warning.model.WarningMessage;
import com.pr.sepp.notify.warning.model.WarningQuery;
import com.pr.sepp.notify.warning.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static com.pr.sepp.common.constants.CommonParameter.*;
import static com.pr.sepp.common.websocket.GlobalSession.attributesFetch;

@Component("warningFetch")
public class WarningFetch implements BaseFetch<WarningMessage> {

	@Autowired
	private WarningService warningService;

	@Override
	public PageInfo<WarningMessage> fetch(WebSocketSession session) {
		Optional<Integer> optionPageNum = attributesFetch(session, WARNING_PAGE_NUM);
		Integer pageNum = optionPageNum.orElse(1);
		Optional<Integer> optionPageSize = attributesFetch(session, WARNING_PAGE_SIZE);
		Integer pageSize = optionPageSize.orElse(500);
		Optional<Integer> optionalProductId = attributesFetch(session, PRODUCT_ID);
		Optional<Integer> optionalUserId = attributesFetch(session, USER_ID);

		WarningQuery warningQuery = new WarningQuery();
		warningQuery.setProductId(optionalProductId.get());
		warningQuery.setTo(optionalUserId.get());
		warningQuery.setIsSent(0);

		return warningService.warningListPaging(warningQuery, pageNum, pageSize);
	}
}
