package com.pr.sepp.notify.warning.model;

import lombok.Data;

@Data
public class WarningNotify {
	private Integer id;
	private Integer warningId;
	private Integer to;
	private Integer sendGateway;
}
