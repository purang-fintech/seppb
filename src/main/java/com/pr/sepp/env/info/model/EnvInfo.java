package com.pr.sepp.env.info.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "envTypeName")
public class EnvInfo {
	private Integer id;
	private Integer productId;
	private Integer branchId;
	private Integer envType;
	private String instance;
	private String envUrl;
	private String jobName;
	private String jobParams;
	private Integer qrCode;
	private String envTypeName;
	private String branchName;

}