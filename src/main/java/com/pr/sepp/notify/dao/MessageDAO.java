package com.pr.sepp.notify.dao;

import com.pr.sepp.notify.model.Mail;
import com.pr.sepp.notify.model.Message;
import com.pr.sepp.notify.model.MessageFlow;
import com.pr.sepp.notify.model.req.HaveReadMessageReq;
import com.pr.sepp.notify.model.resp.MessageResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageDAO {

    List<MessageResp> findMessagesByUser(@Param("messageQuery") HaveReadMessageReq messageQuery);

    List<Mail> mailNotSentQuery(List<String> list);

    int createMessage(Message message);

    int batchCreateMessageFlow(List<MessageFlow> messageFlows);

    void updateMessageToHaveRead(@Param("ids") List<Long> ids);

    void UpdateAllMessageToHaveReadByUser(Integer userId);

    void updateMessageDeliveryStatus(Integer userId, Integer messageType);

    void updateMailDeliveryStatus(Long id, Integer status);
}
