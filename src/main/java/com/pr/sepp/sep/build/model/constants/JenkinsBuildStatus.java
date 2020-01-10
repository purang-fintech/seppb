package com.pr.sepp.sep.build.model.constants;

import com.offbytwo.jenkins.model.BuildWithDetails;

import java.util.Objects;

public enum JenkinsBuildStatus {
	//成功
	START("构建开始", "#8c919a", "el-icon-star-on"),
	SUCCESS("构建成功", "#6bbd6b", "el-icon-success"),
	BUILDING("构建中", "unknown", "el-icon-star-on"),
	QUEUE("排队中", "black", "el-icon-star-on"),
	FAILURE("构建失败", "#EE6F6F", "el-icon-error"),
	UNSTABLE("构建不稳定", "#E6A23C", "el-icon-warning"),
	ABORTED("被取消", "#8c919a", "el-icon-circle-close");
	public String statusCh;
	public String color;
	public String badge;

	JenkinsBuildStatus(String statusCh, String color, String badge) {
		this.statusCh = statusCh;
		this.color = color;
		this.badge = badge;
	}

	public static JenkinsBuildStatus buildToBuildStatus(BuildWithDetails details) {
//        BuildWithDetails details = build.details();
//        BuildResult result = details.getResult();
		JenkinsBuildStatus status;
		if (details.isBuilding()) {
			status = JenkinsBuildStatus.BUILDING;
		} else if (Objects.isNull(details.getResult())) {
			status = QUEUE;
		} else {
			status = JenkinsBuildStatus.valueOf(details.getResult().name());
		}
		return status;
	}
}
