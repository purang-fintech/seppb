package com.pr.sepp.sep.build.model.sonar;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SonarScan {
	private Integer id;
	private Integer noteId;
	private String projectKey;
	private String instanceName;
	private Integer productId;
	private String gitBranch;
	private String projectVersion;
	private Integer resultId;
	private Integer submitter;
	private LocalDateTime startTime;
}
