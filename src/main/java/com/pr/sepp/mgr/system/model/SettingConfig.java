package com.pr.sepp.mgr.system.model;

import lombok.Data;

@Data
public class SettingConfig {
	private Integer id;
	private String settingType;
	private String settingName;
	private Integer settingLimit;
	private String settingKeys;
}