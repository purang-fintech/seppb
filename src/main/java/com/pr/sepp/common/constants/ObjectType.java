package com.pr.sepp.common.constants;

public enum ObjectType {
	CASE(1, "测试用例"),
	REQUEST(2, "产品需求"),
	RELEASENOTE(3, "发布清单"),
	RELEASE(4, "版本计划"),
	DEFECT(5, "产品缺陷"),
	CMS(6, "开发任务"),
	TESTPLAN(7, "测试计划"),
	TESTRPT(8, "测试报告"),
	SCENARIO(9, "测试场景"),
	USER(10, "用户信息"),
	PRODUCT(11, "产品信息"),
	MODULE(12, "产品模块"),
	PRIV(13, "用户权限"),
	ENV(14, "环境信息"),
	CASESTEP(15, "用例步骤"),
	TEAM(16, "团队信息"),
	PROBLEM(17, "线上问题"),
	TMS(18, "测试任务"),
	PRDOCS(19, "产品文档"),
	BRANCH(20, "产品分支"),
	REQBPMN(21, "需求审批"),
	BUILD(22, "构建发布"),
	WARNING(23, "质量预警"),
	UNKNOWN(0, "未知");
	private Integer key;
	private String name;

	ObjectType(Integer key, String name) {
		this.key = key;
		this.name = name;
	}

	public Integer getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public static ObjectType enumByKey(Integer key) {
		for (ObjectType value : values()) {
			if (value.getKey().equals(key)) {
				return value;
			}
		}
		return UNKNOWN;
	}

}
