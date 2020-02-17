package com.pr.sepp.sep.build.service;

import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.sonar.*;
import org.gitlab.api.models.GitlabBranch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface SonarScanService {

	void saveSonarData(SonarScanReq sonarScanreq);

	boolean handleConfig() throws IOException;

	int saveSonarResult(SonarResult sonarResult);

	void syncProjectSuccess(Integer scanId, Integer id);

	List<SonarScan> getSyncList();

	List<SonarProjectNames> listSonarProjectNames(Integer noteId);

	List<SonarReqInstance> listInstance(Integer productId);

	List<SonarScanHistory> sonarAllScanHistory(Integer productId, String projectKey);

	List<GitlabBranch> listBranch(BuildInstance buildInstance) throws IOException;

}
