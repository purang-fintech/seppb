package com.pr.sepp.notify.message.model.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResp {
	private Long id;
	private Integer productId;
	private String title;
	private String content;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime createdDate;
	private Integer objectType;
	private String objectTypeName;
	private String creator;
	private Integer objectId;
	private boolean isSent;
	private String userName;
}
