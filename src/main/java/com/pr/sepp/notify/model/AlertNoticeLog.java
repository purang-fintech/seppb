package com.pr.sepp.notify.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlertNoticeLog {

    private Long id;
    private String warningIds;
    private Integer sendGateway;
    private String from;
    private Integer to;
    private LocalDateTime sendTime;
    private LocalDateTime sentTime;
    private Boolean isSent;
    private Boolean sendSuccess;
    private Boolean isRead;
    private LocalDateTime updateDate;
    private LocalDateTime createdDate;

}
