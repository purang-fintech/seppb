package com.pr.sepp.sep.testing.model;

import lombok.Data;

@Data
public class TestScenario {
	private Integer id;// 场景 ID
	private String name;// 场景名
	private Integer creator; // 创建人
	private Integer planId;// 测试计划ID
	private String cases;// 测试用例ID集合
}