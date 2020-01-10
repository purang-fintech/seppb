package com.pr.sepp.sep.build.model.sonar;

import lombok.Data;

@Data
public class SonarResult {

	private Integer id;
	private String projectKey;
	private String projectVersion;
	private String key;
	private String scanDate;
	private String analysisStatus;
	private Double ncloc;
	private Double coverage;
	private Double duplicatedLinesDensity;
	private Double codeSmells;
	private Double bugs;
	private Double vulnerabilities;
	private Double sqaleIndex;
}


