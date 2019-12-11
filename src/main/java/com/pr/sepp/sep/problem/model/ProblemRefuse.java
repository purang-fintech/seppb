package com.pr.sepp.sep.problem.model;

import lombok.Data;

@Data
public class ProblemRefuse {
	private Integer id;
	private Integer status;
	private Integer typeFirst;
	private Integer typeSecond;
	private Integer resolveMethod;
	private String refuseReason;
	private String closeTime;
}