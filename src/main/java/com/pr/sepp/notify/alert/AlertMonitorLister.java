package com.pr.sepp.notify.alert;

import com.pr.sepp.notify.model.AlertNoticeLog;
import com.pr.sepp.notify.model.AlertNotifyBo;
import com.pr.sepp.notify.service.AlertService;
import com.pr.sepp.utils.JavaMailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AlertMonitorLister implements ApplicationListener<AlertMonitorEvent> {

    @Autowired
    private AlertService alertService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void onApplicationEvent(AlertMonitorEvent event) {
        AlertNotifyBo alertNotifyBo = (AlertNotifyBo) event.getSource();
        boolean success;
        try {
            success = JavaMailUtils.sendMail(alertNotifyBo.getMailDTO());
        } catch (Exception e) {
            success = false;
            log.error("send mail failed {}", e);
        }
        //持久化发件记录
        AlertNoticeLog log = AlertNoticeLog.builder().warningIds(alertNotifyBo.getWarningIds())
                .from(fromEmail)
                .to(alertNotifyBo.getTo())
                .isRead(success)
                .isSent(success)
                .sendGateway(2)
                .sendTime(LocalDateTime.now())
                .sendSuccess(success)
                .build();
        alertService.insertAlertNotify(log);

    }
}