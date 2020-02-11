package com.pr.sepp.notify.warning.model;

import lombok.Data;

@Data
public class WarningBatch {
	private Integer id;
	private Long batchNo;
	private String warningDate;
	private String category;
}
