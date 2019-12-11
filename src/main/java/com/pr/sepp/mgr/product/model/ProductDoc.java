package com.pr.sepp.mgr.product.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"uploadUserName", "maintainUserName", "url", "reqSummary"})
public class ProductDoc {
	private Integer id;
	private Integer reqId;
	private String reqSummary;
	private Integer productId;
	private Integer moduleId;
	private String label;
	private String type;
	private Integer parentId;
	private Integer attachmentId;
	private Integer maintainUser;
	private String keyword;
	private String summary;
	private String version;
	private String fileName;
	private Integer uploadUser;
	private String uploadDate;

	private String uploadUserName;
	private String maintainUserName;
	private String url;

}