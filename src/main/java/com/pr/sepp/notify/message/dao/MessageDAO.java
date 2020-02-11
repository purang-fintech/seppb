package com.pr.sepp.notify.message.dao;

import com.pr.sepp.notify.message.model.Message;
import com.pr.sepp.notify.message.model.MessageFlow;
import com.pr.sepp.notify.message.model.MessageMail;
import com.pr.sepp.notify.message.model.req.HaveReadMessageReq;
import com.pr.sepp.notify.message.model.resp.MessageResp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageDAO {

    List<MessageResp> findMessagesByUser(@Param("messageQuery") HaveReadMessageReq messageQuery);

    List<MessageMail> mailNotSentQuery(List<String> list);

    int createMessage(Message message);

    int batchCreateMessageFlow(List<MessageFlow> messageFlows);

    void updateMessageToHaveRead(@Param("ids") List<Long> ids);

    void UpdateAllMessageToHaveReadByUser(Integer userId);

    void updateMessageDeliveryStatus(Integer userId, Integer messageType);

    void updateMailDeliveryStatus(Long id, Integer status);
}
