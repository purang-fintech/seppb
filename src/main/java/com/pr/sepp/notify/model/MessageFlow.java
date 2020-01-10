package com.pr.sepp.notify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageFlow {
    private Long id;
    private Long messageId;
    private Integer creator;
    private Integer type;
    private Integer userId;
    private Integer isRead;
}
