package com.pr.sepp.notify.fetch;

import com.pr.sepp.notify.model.Alert;
import com.pr.sepp.notify.service.AlertService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static com.pr.sepp.common.constants.CommonParameter.*;
import static com.pr.sepp.common.websocket.GlobalSession.attributesFetch;

@Component("alertFetch")
public class AlertInfoFetch implements BaseFetch<Alert> {

    @Autowired
    private AlertService alertService;

    @Override
    public PageInfo<Alert> fetch(WebSocketSession session) {
        Optional<Integer> optionPageNum = attributesFetch(session, ALERT_PAGE_NUM);
        Integer pageNum = optionPageNum.orElse(1);
        Optional<Integer> optionPageSize = attributesFetch(session, ALERT_PAGE_SIZE);
        Integer pageSize = optionPageSize.orElse(10);
        Optional<Integer> optionalProductId = attributesFetch(session, PRODUCT_ID);
        return optionalProductId.map(productId -> alertService.alertListPaging(productId, pageNum, pageSize))
                .orElseGet(PageInfo::new);
    }

}
