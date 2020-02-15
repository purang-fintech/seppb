package com.pr.sepp.notify.message.service;

import com.pr.sepp.common.constants.MailDTO;
import com.pr.sepp.notify.message.dao.MessageDAO;
import com.pr.sepp.notify.message.model.MessageMail;
import com.pr.sepp.utils.JavaMailUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class MessageMailService {

	@Autowired
	private MessageDAO messageDAO;

	@Value("${fastdfs.server}")
	private String domain;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Value("${spring.mail.whiteList}")
	private String whiteList;

	private static final String EMAIL_HEADER = "<div style='font-family:微软雅黑'>%s，您好，如下信息请您知晓，谢谢：</div>" +
			"<div style='margin:20px 0 0 40px;font-family:微软雅黑'>项目产品：<b>%s</b></div>" +
			"<div style='margin:10px 0 0 40px;font-family:微软雅黑'>操作用户：<b>%s</b></div>" +
			"<div style='margin:10px 0 0 40px;font-family:微软雅黑'>操作内容：<b>%s</b></div>";

	private static final String EMAIL_FOOTER = "<br><br><br><br>" +
			"<div style='float:right;text-align:right;width:100%%;font-family:微软雅黑'>详细情况请前往 <a href='%s'>普兰能效平台</a> 查看</div>" +
			"<div style='float:right;text-align:right;width:100%%;font-family:微软雅黑'>若默认浏览器非Chrome，请复制链接使用Chrome打开</div>" +
			"<div style='float:right;text-align:right;width:100%%;font-family:微软雅黑'>此消息为系统自动发送，请勿直接回复！</div>";

	public void sendMail(){
		List<MessageMail> mailList = messageDAO.mailNotSentQuery(StringUtils.isEmpty(whiteList) ? null : Arrays.asList(whiteList.split(",")));
		if (null == mailList || mailList.size() == 0) {
			return;
		}

		String rootUrl = domain.substring(0, domain.lastIndexOf(":")) + "/#/index";

		mailList.forEach(mail -> {
			String header = String.format(EMAIL_HEADER, mail.getUserName(), mail.getProductName(), mail.getCreatorName(), mail.getContent());
			String body = String.format(EMAIL_FOOTER, rootUrl);
			MailDTO mailDTO = MailDTO.builder().from(fromEmail)
					.to(new String[]{mail.getUserEmail()})
					.content(header + body)
					.isHtml(true)
					.subject("【" + mail.getObjectTypeName() + "】" + mail.getTitle())
					.build();
			try {
				JavaMailUtils.sendMail(mailDTO);
				messageDAO.updateMailDeliveryStatus(mail.getId(), 1);
			} catch (Exception ne) {
				if (ne.getMessage().indexOf("550 User not found") > 0) {
					log.warn("邮件地址无效，请联系用户确认！", ne.getMessage());
					messageDAO.updateMailDeliveryStatus(mail.getId(), 2);
					return;
				} else {
					log.error("邮件发送失败！", ne);
					return;
				}
			}
		});
	}
}
