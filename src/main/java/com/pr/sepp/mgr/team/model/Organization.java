package com.pr.sepp.mgr.team.model;

import lombok.Data;

@Data
public class Organization {
	private Integer id;
	private Integer parentId;
	private Integer responser;
	private String teamName;
	private String teamDescription;
}