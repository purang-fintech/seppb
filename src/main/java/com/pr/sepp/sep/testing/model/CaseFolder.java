package com.pr.sepp.sep.testing.model;

import lombok.Data;

@Data
public class CaseFolder {
	private Integer id;
	private Integer parentId;
	private Integer productId;
	private String name;
	private String type;
	private Integer creator;
}