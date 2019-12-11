package com.pr.sepp.mgr.product.model;

import lombok.Data;

@Data
public class ProductBranch {
	private Integer productId;
	private Integer branchId;
	private String branchName;
	private Integer isValid;
	private Integer creator;
}