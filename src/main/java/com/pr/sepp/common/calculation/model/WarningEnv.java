package com.pr.sepp.common.calculation.model;

import lombok.Data;

@Data
public class WarningEnv {

	private Long stgConfigLost;
	private Long prdConfigLost;
	private Long stgConfigNull;
	private Long prdConfigNull;

	private Long stgMonAlertHandled;
	private Long stgMonAlertCount;
}
