package com.pr.sepp.base.model;

import lombok.Data;

@Data
public class DefectStatus {
	private Integer id;
	private Integer statusId;
	private String statusName;
	private Integer newStatusId;
	private String newStatusName;
	private String statusTips;
}
