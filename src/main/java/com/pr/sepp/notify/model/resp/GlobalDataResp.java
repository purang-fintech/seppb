package com.pr.sepp.notify.model.resp;

import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.websocket.model.MessageType;
import com.pr.sepp.notify.model.Alert;
import com.pr.sepp.notify.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.pr.sepp.common.websocket.model.MessageType.ALARM;
import static com.pr.sepp.common.websocket.model.MessageType.NOTICE;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalDataResp {
    private PageInfo<Alert> alerts = new PageInfo<>();
    private PageInfo<Message> messages = new PageInfo<>();
    private Long newMessageCount;
    private Long newAlertCount;

    public GlobalDataResp apply(PageInfo info, MessageType type) {
        if (type == ALARM) {
            setAlerts(info);
        }
        if (type == NOTICE) {
            setMessages(info);
            setNewMessageCount(newMessageFrom(info.getList()));
        }
        return this;
    }

    public static Long newMessageFrom(List<MessageResp> messageResps) {
        return messageResps.stream().filter(messageResp -> !messageResp.isSent()).count();
    }
}
