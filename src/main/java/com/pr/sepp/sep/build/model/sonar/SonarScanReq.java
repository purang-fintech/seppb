package com.pr.sepp.sep.build.model.sonar;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SonarScanReq {
	private Integer id;
	private Integer noteId;
	private Integer productId;
	private String instanceName;
	private List<String> projectKeys;
	private String gitBranch;
	private String language;
	private String projectVersion;
	private Integer resultId;
	private Integer submitter;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	public void InitData() {

		this.setStartTime(LocalDateTime.now());

	}
}
