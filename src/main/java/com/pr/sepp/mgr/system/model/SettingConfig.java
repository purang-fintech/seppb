package com.pr.sepp.mgr.system.model;

import lombok.Data;

@Data
public class SettingConfig {
	private int id;
	private String settingName;
	private int settingLimit;
	private String settingKeys;
}