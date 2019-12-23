package com.pr.sepp.notify.alert;

import com.pr.sepp.common.constants.MailDTO;
import com.pr.sepp.notify.dao.AlertDAO;
import com.pr.sepp.notify.model.AlertNotice;
import com.pr.sepp.notify.model.AlertNotifyBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pr.sepp.common.constants.AlertLevel.*;
import static com.pr.sepp.notify.model.AlertNotice.buildTableHeader;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Component
public class AlertMonitorHelper {

    private static final String TABLE_CLASS = "class";
    private static final String TABLE_CLASS_VALUE = "hovertable";
    private static final String ALERT_NOTICE_MESSAGE = "昨日有%d条告警：致命告警%d条、严重告警%d条、重要告警%d条、一般告警%d条";
    private static final String ALERT_SUBJCT = "告警日报";
    private static final String ALERT_RETRY_ERROR_TITLE = "告警日报发送失败通知";
    @Autowired
    private AlertDAO alertDAO;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Value("classpath:css/alertTable.css")
    private Resource css;
    @Value("${spring.mail.username}")
    private String fromEmail;


    @Scheduled(cron = "0 0 7 * * ? ")
    public void start() {
        pushEventAll();
    }

    public void pushEventAll() {
        List<AlertNotice> alertNotices = alertDAO.findAllAlertsOfToday();
        Map<String, List<AlertNotice>> userAlertNotice = alertNotices.stream().collect(Collectors.groupingBy(AlertNotice::getUserEmail));
        userAlertNotice.forEach(this::pushEventByEmail);
    }

    public void pushEventByEmail(String email, List<AlertNotice> alertNotices) {
        try {
            MailDTO mailDTO = mailDTOBuilder(email, alertNotices);
            Set<Long> ids = alertNotices.stream().map(AlertNotice::getWarningId).collect(toSet());
            AlertNotifyBo alertNotifyBo = AlertNotifyBo.builder()
                    .mailDTO(mailDTO)
                    .warningIds(ids.toString())
                    .to(alertNotices.get(0).getUserId())
                    .build();
            AlertMonitorEvent<AlertNotifyBo> alertMonitorEvent = new AlertMonitorEvent<>(alertNotifyBo);
            alertMonitorEvent.push(applicationEventPublisher::publishEvent);
        } catch (Exception e) {
            log.error("告警推送失败:{}", e);
        }
    }

    private MailDTO mailDTOBuilder(String email, List<AlertNotice> alertNotices) throws IOException {
        return MailDTO.builder()
                .failRetry(true)
                .failRetryCount(3)
                .failRetryTime(10_1000)
                .failSubject(ALERT_RETRY_ERROR_TITLE)
                .failTo(new String[]{"chenlang@purang.com"})
                .to(new String[]{email})
                .from(fromEmail)
                .content(createHtml(alertNotices).toHtml())
                .isHtml(true)
                .subject(ALERT_SUBJCT)
                .isBlock(true)
                .build();
    }

    private String notifyMessage(List<AlertNotice> alertNotices) {
        Map<Integer, List<AlertNotice>> alertLevel = alertNotices.stream().collect(Collectors.groupingBy(AlertNotice::getLevel));
        return String.format(ALERT_NOTICE_MESSAGE, alertNotices.size()
                , nullToZero(alertLevel.get(FATAL.getKey())), nullToZero(alertLevel.get(SERIOUS.getKey())),
                nullToZero(alertLevel.get(IMPORTANT.getKey())), nullToZero(alertLevel.get(GENERAL.getKey())));
    }

    private static Integer nullToZero(List<AlertNotice> value) {
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

    public HtmlCanvas createHtml(List<AlertNotice> alertNotices) throws IOException {
        HtmlCanvas html = new HtmlCanvas();
        HtmlAttributes attributes = new HtmlAttributes();
        attributes.add(TABLE_CLASS, TABLE_CLASS_VALUE);
        html.html()
                .style().content(IOUtils.toString(css.getInputStream()))
                .body().table(attributes);

        html.span(new HtmlAttributes("style", "color:red")).content(notifyMessage(alertNotices)).br();
        buildTableHeader(html);
        HtmlAttributes style = new HtmlAttributes();
        style.add("onmouseout", "this.style.backgroundColor=\'#d4e3e5\';");
        for (AlertNotice alertNotice : alertNotices) {
            html.tr(style).td().content(alertNotice.getType())
                    .td().content(alertNotice.getCategory())
                    .td().content(alertNotice.getWarningDate().toString())
                    .td().content(enumByKey(alertNotice.getLevel()).getName())
                    .td().content(subContent(alertNotice.getContent()))._tr();
        }
        html._table()._body()._html();
        return html;
    }

}
