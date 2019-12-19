package com.pr.sepp.mgr.product.model;

import lombok.Data;

@Data
public class Product {
	private Integer productId;
	private Integer owner;
	private Integer userId;
	private String productCode;
	private String productName;
	private String productDesc;
	private String ownerName;
	private String isValid;
}