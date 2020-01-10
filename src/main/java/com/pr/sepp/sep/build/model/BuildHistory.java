package com.pr.sepp.sep.build.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.offbytwo.jenkins.model.BuildChangeSet;
import com.offbytwo.jenkins.model.BuildChangeSetItem;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.pr.sepp.sep.build.model.constants.BuildType;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.sep.build.model.constants.JenkinsBuildStatus;
import com.pr.sepp.sep.build.model.req.BuildHistoryReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.pr.sepp.sep.build.model.constants.JenkinsBuildStatus.START;
import static com.pr.sepp.sep.build.model.constants.JenkinsBuildStatus.buildToBuildStatus;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildHistory {
	private Integer id;
	private String jobName;
	private Integer noteId;
	private Integer productId;
	private Integer branchId;
	private Integer envType;
	private String instance;
	private Integer buildVersion;
	private String submitter;
	private String buildHost;
	private JenkinsBuildStatus buildStatus;
	private String buildParams;
	private Long buildInterval;
	private String codeChange;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private String createdDate;
	private LocalDateTime updatedDate;
	private String tag;

	private Integer userId;
	private JenkinsStatus status;
	private String branchName;
	private String envName;
	private String pipelineStep;
	private BuildType buildType;

	private InstanceType type;

	public void setBuildStatus(JenkinsBuildStatus buildStatus) {
		this.buildStatus = buildStatus;
		this.status = JenkinsStatus.apply(buildStatus);
	}

	@Data
	@Builder
	public static class JenkinsStatus {
		private String color;
		@Builder.Default
		private Integer percentage = 100;
		private String badge;
		private String statusCh;

		public static JenkinsStatus apply(JenkinsBuildStatus status) {
			if (Objects.isNull(status)) return null;
			return JenkinsStatus.builder().badge(status.badge).color(status.color).statusCh(status.statusCh).build();
		}

		public void setPercentage(Integer percentage) {
			if (percentage == -1) {
				this.percentage = 90;
				return;
			}
			this.percentage = percentage;
		}
	}

	public static BuildHistory apply(Integer number, BuildWithDetails details, String jobName) {
		return BuildHistory.builder()
				.buildStatus(buildToBuildStatus(details))
				.buildVersion(number)
				.jobName(jobName)
				.codeChange(codeChange(details.getChangeSets()))
				.buildInterval(details.getDuration())
				.build();
	}

	public static BuildHistory reqToBuildHistory(BuildHistoryReq buildHistoryReq) {
		BuildHistory buildHistory = BuildHistory.builder().build();
		BeanUtils.copyProperties(buildHistoryReq, buildHistory);
		buildHistory.setBuildStatus(START);
		return buildHistory;
	}

	public static String codeChange(List<BuildChangeSet> changeSets) {
		if (changeSets != null) {
			List<String> msg = changeSets.stream()
					.flatMap(changeSet -> changeSet.getItems().stream().map(BuildChangeSetItem::getMsg))
					.collect(toList());
			return StringUtils.join(msg, "<br>");
		}
		return EMPTY;
	}

}
