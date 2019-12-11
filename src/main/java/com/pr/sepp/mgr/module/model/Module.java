package com.pr.sepp.mgr.module.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"proderName", "deverName", "testerName"})
public class Module {
	private Integer productId;
	private Integer moduleId;
	private String productCode;
	private Integer pdResponser;
	private Integer devResponser;
	private Integer testResponser;
	private String moduleName;
	private String moduleDesc;
	private String codePackage;
	private String isValid;
	private String proderName;
	private String deverName;
	private String testerName;
}