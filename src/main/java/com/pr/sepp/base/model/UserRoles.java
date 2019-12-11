package com.pr.sepp.base.model;

import lombok.Data;

@Data
public class UserRoles {
	private Integer roleId;
	private String roleCode;
	private String roleName;
	private String isValid;
}
