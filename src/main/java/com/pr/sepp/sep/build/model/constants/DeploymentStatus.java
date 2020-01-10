package com.pr.sepp.sep.build.model.constants;

import com.offbytwo.jenkins.model.BuildWithDetails;

import java.util.Objects;

public enum DeploymentStatus {
	/**
	 *
	 */
	START("部署开始", "#8c919a", "el-icon-star-on"),
	DEPLOYING("部署中", "#8c919a", "el-icon-star-on"),
	SUCCESS("部署成功", "#6bbd6b", "el-icon-success"),
	FAILED("部署失败", "#EE6F6F", "el-icon-error"),
	BUILDING("构建中", "unknown", "el-icon-star-on"),
	QUEUE("排队中", "black", "el-icon-star-on"),
	FAILURE("部署失败", "#EE6F6F", "el-icon-warning"),
	UNSTABLE("部署成功", "#6bbd6b", "el-icon-success"),
	ABORTED("部署被取消", "#8c919a", "el-icon-circle-close"),
	RESET("状态重置", "#E6A23C", "el-icon-warning");
	public String value;
	public String color;
	public String badge;

	DeploymentStatus(String value, String color, String badge) {
		this.value = value;
		this.color = color;
		this.badge = badge;
	}

	public static DeploymentStatus convertJenkinsStatus(BuildWithDetails details) {
		DeploymentStatus status;
		if (details.isBuilding() || Objects.isNull(details.getResult())) {
			status = DeploymentStatus.DEPLOYING;
		} else {
			status = DeploymentStatus.valueOf(details.getResult().name());
		}
		return status;
	}
}
