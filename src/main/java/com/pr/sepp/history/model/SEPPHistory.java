package com.pr.sepp.history.model;

import lombok.Data;

@Data
public class SEPPHistory {
	private Integer id;
	private Integer objType;
	private Integer objId;
	private String objKey;
	private Integer productId;
	private Integer operUser;
	private String operTime;
	private Integer operType;
	private Integer referUser;
	private String orgValue;
	private String newValue;
	private String operComment;
}
