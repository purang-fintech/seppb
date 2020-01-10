package com.pr.sepp.sep.build.service.impl;


import com.pr.sepp.sep.build.dao.SonarDAO;
import com.pr.sepp.sep.build.model.sonar.*;
import com.pr.sepp.sep.build.service.SonarScanService;
import com.pr.sepp.utils.jenkins.JenkinsClient;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.pr.sepp.sep.build.model.constants.InstanceType.ORDINARY;

@Service
@Transactional
@Slf4j
public class SonarScanImpl implements SonarScanService {
	@Autowired
	private SonarDAO sonarDAO;

	@Autowired
	private JenkinsClientProvider jenkinsClientProvider;


	@Override
	public void saveSonarData(SonarScanReq sonarScanReq) {
		SonarScan project = new SonarScan();
		sonarScanReq.InitData();
		project.setNoteId(sonarScanReq.getNoteId());
		project.setSubmitter(sonarScanReq.getSubmitter());
		project.setGitBranch(sonarScanReq.getGitBranch());
		project.setProductId(sonarScanReq.getProductId());
		project.setProjectVersion(sonarScanReq.getGitBranch() + "_" + sonarScanReq.getStartTime());
		for (String projectKey : sonarScanReq.getProjectKeys()) {
			project.setProjectKey(projectKey);
			sonarDAO.insertSonarScan(project);
		}


		HashMap<String, String> paramMap = new HashMap<>();
		paramMap.put("projectName", String.join(",", sonarScanReq.getProjectKeys()));
		paramMap.put("gitBranch", sonarScanReq.getGitBranch());
		paramMap.put("startTime", sonarScanReq.getStartTime().toString());
		try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(ORDINARY)) {
			jenkinsClient.startBuild("sonarscanGit", paramMap);
		} catch (Exception e) {
			log.error("", e);
		}

	}

	@Override
	public int saveSonarResult(SonarResult sonarResult) {
		sonarDAO.insertSonarResult(sonarResult);
		int id = sonarResult.getId();
		return id;
	}

	@Override
	public void syncProjectSuccess(Integer scanId, Integer id) {
		sonarDAO.syncProjectSuccess(scanId, id);
	}

	@Override
	public List<SonarScan> getSyncList() {
		return sonarDAO.listUnSyncProject();
	}


	public List<SonarProjectNames> listSonarProjectNames(Integer noteId) {
		return sonarDAO.listSonarProjectNames(noteId);
	}


	@Override
	public List<SonarScanHistory> sonarAllScanHistory(Integer productId, String projectKey) {
		return sonarDAO.ListAllSonarScanHistory(productId, projectKey);
	}

	@Override
	public List<SonarScanHistoryGrouper> sonarScanHistory(Integer noteId, Integer pageNum, Integer pageSize) {
		List<SonarScanHistory> sonarScanHistorys = sonarDAO.ListSonarScanHistory(noteId, pageNum, pageSize);
		List<SonarScanHistoryGrouper> listSonarScanHistoryGrouper = new ArrayList<>();

		Set<String> projectVersions = new HashSet<>();
		sonarScanHistorys.forEach(sonarScanHistory -> {
			projectVersions.add(sonarScanHistory.getProjectVersion());
		});

		for (String projectVersion : projectVersions) {
			SonarScanHistoryGrouper sonarScanHistoryGrouper = new SonarScanHistoryGrouper();
			sonarScanHistoryGrouper.setProjectVersion(projectVersion);
			List<SonarScanHistory> tempItem = new ArrayList<>();

			for (int i = 0; i < sonarScanHistorys.size(); i++) {
				if (projectVersion.hashCode() == sonarScanHistorys.get(i).getProjectVersion().hashCode()) {
					tempItem.add(sonarScanHistorys.get(i));
				}
			}
			sonarScanHistoryGrouper.setSonarScanHistorys(tempItem);
			listSonarScanHistoryGrouper.add(sonarScanHistoryGrouper);
		}
		;
		return listSonarScanHistoryGrouper;
	}
}
