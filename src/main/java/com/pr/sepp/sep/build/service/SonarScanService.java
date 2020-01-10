package com.pr.sepp.sep.build.service;


import com.pr.sepp.sep.build.model.sonar.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SonarScanService {

	void saveSonarData(SonarScanReq sonarScanreq);

	int saveSonarResult(SonarResult sonarResult);

	void syncProjectSuccess(Integer scanId, Integer id);

	List<SonarScan> getSyncList();

	List<SonarProjectNames> listSonarProjectNames(Integer noteId);

	List<SonarScanHistoryGrouper> sonarScanHistory(Integer noteId, Integer pageNum, Integer pageSize);

	List<SonarScanHistory> sonarAllScanHistory(Integer productId, String projectKey);
}
