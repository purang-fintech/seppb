package com.pr.sepp.notify.warning.model;

import lombok.Data;

@Data
public class WarningQuery {
	private Integer id;
	private Integer batchId;
	private Long batchNo;
	private Integer productId;
	private Integer type;
	private Integer subType;
	private Integer level;
	private Integer isSent;
	private Integer to;
	private Integer sendGateway;
	private Integer pageSize;
	private Integer pageNum;

	private String warningDate;
	private String category;
}
