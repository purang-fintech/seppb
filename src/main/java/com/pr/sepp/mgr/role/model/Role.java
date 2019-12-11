package com.pr.sepp.mgr.role.model;

import lombok.Data;

@Data
public class Role {
	private int roleId;
	private String roleCode;
	private String roleName;
	private String isValid;
}