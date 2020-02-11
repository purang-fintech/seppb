package com.pr.sepp.notify.model;

import com.github.pagehelper.PageInfo;
import com.pr.sepp.notify.message.model.resp.MessageResp;
import com.pr.sepp.notify.warning.model.Warning;
import com.pr.sepp.common.websocket.model.MessageType;
import com.pr.sepp.notify.message.model.Message;
import com.pr.sepp.notify.warning.model.WarningMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.pr.sepp.common.websocket.model.MessageType.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalDataResp {
	private PageInfo<Warning> warnings = new PageInfo<>();
	private PageInfo<Message> messages = new PageInfo<>();
	private Long newMessageCount;
	private Long newWarningCount;

	public GlobalDataResp apply(PageInfo info, MessageType type) {
		if (type == WARNING) {
			setWarnings(info);
			setNewWarningCount(newWarningFrom(info.getList()));
		}
		if (type == MESSAGE) {
			setMessages(info);
			setNewMessageCount(newMessageFrom(info.getList()));
		}
		return this;
	}

	public static Long newMessageFrom(List<MessageResp> messageResps) {
		return messageResps.stream().filter(messageResp -> !messageResp.isSent()).count();
	}

	public static Long newWarningFrom(List<WarningMessage> warnings) {
		return warnings.stream().filter(warningMessage -> !warningMessage.isSent()).count();
	}
}
