package com.pr.sepp.notify.warning.model;

import lombok.Data;

@Data
public class WarningMessage {
	private Integer id;
	private Integer batchId;
	private Long batchNo;
	private Integer productId;
	private String productName;
	private boolean isSent;
	private Integer to;
	private String toName;
	private Integer sendGateway;

	private Integer type;
	private String typeName;
	private Integer subType;
	private String subTypeName;
	private Integer level;
	private String levelName;
	private String warningDate;
	private Integer relId;
	private String category;
	private String summary;
	private String content;
}
