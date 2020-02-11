package com.pr.sepp.notify.warning.model;

import lombok.Data;

@Data
public class Warning {
	private Integer id;
	private Integer batchId;
	private Integer productId;
	private Integer type;
	private Integer subType;
	private Integer level;
	private Integer responser;

	private String warningDate;
	private String category;
	private String summary;
	private String content;
}
