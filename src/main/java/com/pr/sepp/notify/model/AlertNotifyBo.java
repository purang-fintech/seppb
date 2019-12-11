package com.pr.sepp.notify.model;

import com.pr.sepp.common.constants.MailDTO;
import lombok.Builder;
import lombok.Data;

import javax.mail.internet.MimeMessage;

@Data
@Builder
public class AlertNotifyBo {
    private String warningIds;
    private MimeMessage mimeMessage;
    private Integer to;
    private MailDTO mailDTO;

}
