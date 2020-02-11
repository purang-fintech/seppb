package com.pr.sepp.mgr.system.constants;

public enum SettingType {
	UNKNOWN(0, "未知"),
	MAIL(1, "邮件服务配置"),
	LDAP(2, "LDAP认证配置"),
	JENKINS(3, "Jenkins配置"),
	GIT(4, "版本管理配置"),
	SONAR(5, "SonarQube配置");

	private Integer value;
	private String text;

	SettingType(Integer value, String text) {
		this.value = value;
		this.text = text;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static SettingType enumByKey(Integer key) {
		for (SettingType value : values()) {
			if (value.getValue().equals(key)) {
				return value;
			}
		}
		return UNKNOWN;
	}
}
