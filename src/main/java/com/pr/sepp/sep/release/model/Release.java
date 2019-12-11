package com.pr.sepp.sep.release.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"creatorName", "responserName", "statusName"})
public class Release {

	private Integer id;
	private Integer branchId;
	private String relCode;
	private String branchName;
	private Integer status;
	private Integer productId;
	private Integer creator;
	private Integer responser;
	private String relDate;
	private String reqConfirmDate;
	private String sitBeginDate;
	private String uatBeginDate;
	private String createDate;
	private String readyDate;
	private String relDateAct;
	private String environment;

	private String creatorName;
	private String responserName;
	private String statusName;

}