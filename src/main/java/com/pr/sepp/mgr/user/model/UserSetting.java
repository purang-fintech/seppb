package com.pr.sepp.mgr.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting {
	private Integer id;
	private Integer userId;
	private Integer messageOn;
	private Integer dialogAutoClose;
	private Integer autoLogin;
	private Integer autoRefresh;
	private Integer tableShowBorder;
	private Integer tablePageSize;
	private String messageSubscribe;
	private String emailSubscribe;
	private String portalConfig;
}