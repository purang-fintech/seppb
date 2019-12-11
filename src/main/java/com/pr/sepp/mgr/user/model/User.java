package com.pr.sepp.mgr.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	private Integer userId;
	private Integer faviconId;
	private Integer teamId;
	private String userName;
	private String userAccount;
	private String password;
	private String userEmail;
	private String isVendor;
	private String isValid;
}