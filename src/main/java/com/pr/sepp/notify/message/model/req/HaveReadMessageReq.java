package com.pr.sepp.notify.message.model.req;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import static java.util.Objects.isNull;

@Data
@Builder
public class HaveReadMessageReq {
    private Integer objectType;
    private String messageFrom;
    private String messageCode;
    private LocalDate[] messageDate;
    @Builder.Default
    private Integer pageSize = 10;
    @Builder.Default
    private Integer pageNum = 1;
    private Integer userId;
    private boolean isRead;
    private Integer productId;
    private String startDate;
    private String endDate;

    public String getStartDate() {
        if (isNull(messageDate)) return null;
        return messageDate[0].toString();
    }

    public String getEndDate() {
        if (isNull(messageDate)) return null;
        return messageDate[1].plusDays(1).toString();
    }
}
