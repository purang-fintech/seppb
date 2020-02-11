package com.pr.sepp.common.calculation.model;

import lombok.Data;

@Data
public class WarningRules {
	private Integer id;
	private Integer type;
	private Integer subType;
	private Integer level;
	private Integer targetType;

	private String title;
	private String expression;
}
