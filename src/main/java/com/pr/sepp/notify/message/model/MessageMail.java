package com.pr.sepp.notify.message.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageMail {
    private Long id;
    private Integer objectType;
    private String objectTypeName;
    private Integer objectId;
    private Integer productId;
    private String productName;
    private String title;
    private String content;
    private String userEmail;
    private Integer creator;
    private String creatorName;
    private String userName;
    private boolean isSent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdDate;
}
