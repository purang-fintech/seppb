package com.pr.sepp.notify.message.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id;
    private Integer objectType;
    private Integer objectId;
    private Integer productId;
    private String title;
    private String content;
    private LocalDateTime updatedDate;
    private LocalDateTime createdDate;
}
