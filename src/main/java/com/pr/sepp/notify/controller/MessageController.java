package com.pr.sepp.notify.controller;

import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.notify.model.req.HaveReadMessageReq;
import com.pr.sepp.notify.model.resp.MessageResp;
import com.pr.sepp.notify.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class MessageController {
	@Autowired
	private MessageService messageService;

	@PostMapping("/message/have-read")
	public void messageHaveRead(@RequestBody Map<String, List<Long>> idMap) {
		messageService.messageHaveRead(idMap.get("ids"));
	}

	@PostMapping("/message/have-read/all")
	public void messageAllHaveRead() {
		messageService.messageAllHaveReadByUser(ParameterThreadLocal.getUserId());
	}

	@PostMapping("/message/have-read::query")
	public PageInfo<MessageResp> readMessagesByUser(@RequestBody HaveReadMessageReq haveReadMessageReq) {
		return messageService.readMessagesByUser(haveReadMessageReq);
	}
}
