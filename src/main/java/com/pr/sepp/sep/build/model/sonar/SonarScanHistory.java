package com.pr.sepp.sep.build.model.sonar;

import lombok.Data;

@Data
public class SonarScanHistory {

	private Integer id;
	private Integer NoteId;
	private Integer productId;
	private String submitter;
	private String projectKey;
	private String gitBranch;
	private String startTime;
	private String projectVersion;
	private String analysisStatus;
	private Double ncloc;
	private Double coverage;
	private Double duplicatedLinesDensity;
	private Double codeSmells;
	private Double bugs;
	private Double vulnerabilities;
	private Double sqaleIndex;

	private Double sqaleIndex(Double sqaleIndex) {
		this.sqaleIndex = sqaleIndex / 60;
		return sqaleIndex;
	}
}


