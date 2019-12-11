package com.pr.sepp.notify.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Data
public class Mail {
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
    private LocalDateTime updatedDate;
    private LocalDateTime createdDate;
}
