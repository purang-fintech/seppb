package com.pr.sepp.sep.build.model.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum InstanceType {
	//应用类型
	IOS("ios实例", "iosJenkinsClient"),
	ANDROID("安卓应用", "androidJenkinsClient"),
	ORDINARY("普通应用", "webJenkinsClient");
	private String value;
	private String beanName;

	InstanceType(String value, String beanName) {
		this.value = value;
		this.beanName = beanName;
	}

	public String getName() {
		return this.value;
	}

	public String getBeanName() {
		return this.beanName;
	}

	public static List<Tag> getTags() {
		return Arrays.stream(InstanceType.values())
				.map(Tag::copy)
				.collect(Collectors.toList());
	}

	public static class Tag {
		private String name;
		private String value;


		public static Tag copy(InstanceType instanceType) {
			Tag tag = new Tag();
			tag.setName(instanceType.name());
			tag.setValue(instanceType.getName());
			return tag;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
