package com.pr.sepp.sep.defect.model;

import lombok.Data;

@Data
public class DefectRequestParam {
	private Integer id;
	private Integer status;
	private Integer responser;
	private String detail;
	private Integer fixTimes;
	private Integer refuseReason;
	private String refuseDetail;
	private Integer sameCodeDefect;
}