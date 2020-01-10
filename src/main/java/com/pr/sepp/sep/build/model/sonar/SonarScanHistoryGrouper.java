package com.pr.sepp.sep.build.model.sonar;

import lombok.Data;

import java.util.List;

@Data
public class SonarScanHistoryGrouper {

	private String projectVersion;
	private List<SonarScanHistory> sonarScanHistorys;


}


