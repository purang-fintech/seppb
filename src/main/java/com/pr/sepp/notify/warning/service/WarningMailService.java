package com.pr.sepp.notify.warning.service;

import com.pr.sepp.common.constants.MailDTO;
import com.pr.sepp.notify.warning.dao.WarningDAO;
import com.pr.sepp.notify.warning.model.WarningMail;
import com.pr.sepp.utils.JavaMailUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.nullToEmpty;
import static com.pr.sepp.common.constants.WarningLevel.*;
import static com.pr.sepp.notify.warning.model.WarningMail.buildTableHeader;

@Slf4j
@Service
public class WarningMailService {

	private static final String TABLE_CLASS = "class";
	private static final String TABLE_CLASS_VALUE = "hovertable";
	private static final String WARNING_MESSAGE_MESSAGE = "%s，您好，今日有%d条与您工作相关的告警：致命告警%d条、严重告警%d条、重要告警%d条、一般告警%d条";
	private static final String WARNING_SUBJCT = "研发过程监控预警日报";
	private static final String WARNING_RETRY_ERROR_TITLE = "告警日报发送失败通知";

	private static final String FOOTER_STYLE = "float:right;text-align:right;width:100%%;font-family:微软雅黑";

	@Autowired
	private WarningDAO warningDAO;

	@Value("classpath:css/warning.css")
	private Resource css;

	@Value("${fastdfs.server}")
	private String domain;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Value("${spring.mail.whiteList}")
	private String whiteList;

	public void sendWarningMail() {
		List<Integer> receivers = warningDAO.distinctEmailRecivers();
		List<Integer> toSendUsers = new ArrayList<>();
		if (StringUtils.isNotEmpty(whiteList)) {
			Arrays.asList(whiteList.split(",")).forEach(userId -> {
				toSendUsers.add(Integer.valueOf(userId));
			});
		}
		receivers.forEach(receiver -> {
			if (toSendUsers.size() > 0 && !toSendUsers.contains(receiver)) {
				return;
			}
			List<WarningMail> warningMails = warningDAO.warningMailSendQuery(receiver);
			if (null == warningMails || warningMails.isEmpty()) {
				return;
			}
			String toEmail = warningMails.stream().filter(f -> Objects.equals(f.getTo(), receiver)).findFirst().orElse(new WarningMail()).getToEmail();
			if (StringUtils.isEmpty(toEmail)) {
				return;
			}
			try {
				JavaMailUtils.sendMail(mailDTOBuilder(toEmail, warningMails));
				warningDAO.updateWarningMailSendStatus(receiver, 1);
			} catch (Exception e) {
				if (e.getMessage().contains("550 User not found")) {
					log.warn("邮件地址无效，请联系用户确认！", e);
					warningDAO.updateWarningMailSendStatus(receiver, 2);
				} else {
					log.error("邮件发送失败！", e);
				}
				return;
			}
		});
	}

	private MailDTO mailDTOBuilder(String email, List<WarningMail> WarningMails) throws IOException {
		return MailDTO.builder()
				.failRetry(false)
				.failSubject(WARNING_RETRY_ERROR_TITLE)
				.failTo(new String[]{"chenlang@purang.com", "liuyi@purang.com", "chencheng3@purang.com"})
				.to(new String[]{email})
				.from(fromEmail)
				.content(createHtml(WarningMails).toHtml())
				.isHtml(true)
				.subject(WARNING_SUBJCT)
				.isBlock(true)
				.build();
	}

	private String notifyMessage(List<WarningMail> warningMails) {
		String userName = warningMails.get(0).getToName();
		Map<Integer, List<WarningMail>> WarningLevel = warningMails.stream().collect(Collectors.groupingBy(WarningMail::getLevel));
		return String.format(WARNING_MESSAGE_MESSAGE, userName, warningMails.size()
				, nullToZero(WarningLevel.get(FATAL.getKey())), nullToZero(WarningLevel.get(SERIOUS.getKey())),
				nullToZero(WarningLevel.get(IMPORTANT.getKey())), nullToZero(WarningLevel.get(GENERAL.getKey())));
	}

	private static Integer nullToZero(List<WarningMail> value) {
		return value == null ? 0 : value.size();
	}

	private static String subContent(String content) {
		content = nullToEmpty(content);
		if (content.length() > 1024) {
			content = content.substring(0, 1024);
			return content.concat("....");
		}
		return content;
	}

	public HtmlCanvas createHtml(List<WarningMail> warningMails) throws IOException {
		String rootUrl = domain.substring(0, domain.lastIndexOf(":")) + "/#/index";

		HtmlCanvas html = new HtmlCanvas();
		html.html().style().content(IOUtils.toString(css.getInputStream())).body();
		html.div(new HtmlAttributes("style", "font-family:微软雅黑;font-size:14px")).content(notifyMessage(warningMails)).br();

		HtmlAttributes attributes = new HtmlAttributes();
		attributes.add(TABLE_CLASS, TABLE_CLASS_VALUE);
		html.table(attributes);
		buildTableHeader(html);
		for (WarningMail WarningMail : warningMails) {
			html.tr().td().content(WarningMail.getProductName())
					.td().content(WarningMail.getCategory())
					.td().content(WarningMail.getWarningDate())
					.td().content(WarningMail.getTypeName())
					.td().content(WarningMail.getSubTypeName())
					.td().content(WarningMail.getLevelName())
					.td().content(WarningMail.getToName())
					.td().content(subContent(WarningMail.getSummary()))._tr();
		}
		html._table();
		html.br().br().br().br();

		HtmlAttributes footerAttr = new HtmlAttributes();
		footerAttr.add("style", FOOTER_STYLE);
		HtmlAttributes footerLinkAttr = new HtmlAttributes();
		footerLinkAttr.add("style", FOOTER_STYLE);
		footerLinkAttr.add("href", rootUrl);

		html.a(footerLinkAttr).content("普兰能效平台").div(footerAttr).content("详细情况查看请前往：").br();
		html.div(footerAttr).content("若默认浏览器非Chrome，请复制链接使用Chrome打开").br();
		html.div(footerAttr).content("此消息为系统自动发送，请勿直接回复！").br();
		html._body()._html();
		return html;
	}
}
