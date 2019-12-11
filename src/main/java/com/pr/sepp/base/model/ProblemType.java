package com.pr.sepp.base.model;

import lombok.Data;

@Data
public class ProblemType {
	private Integer id;
	private Integer type;
	private String typeDesc;
	private Integer subType;
	private String subDesc;
}
