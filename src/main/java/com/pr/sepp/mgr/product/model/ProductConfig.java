package com.pr.sepp.mgr.product.model;

import lombok.Data;

@Data
public class ProductConfig {
	private Integer productId;
	private String memberConfig;
	private String changeAuditor;
	private String gompertzDefine;
	private String gompertzParams;
	private String dreTarget;
}