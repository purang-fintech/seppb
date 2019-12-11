package com.pr.sepp.sep.change.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "changeUserName")
public class Change {
	private Integer id;
	private Integer reqId;
	private String changeTime;
	private Integer changeStatus;
	private Integer changeUser;
	private String auditUser;
	private String audittedUser;
	private String changeDesc;
	private String changeUserName;
	private String changeDetail;

}