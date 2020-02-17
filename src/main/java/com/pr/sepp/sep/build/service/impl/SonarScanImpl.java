package com.pr.sepp.sep.build.service.impl;

import com.pr.sepp.mgr.system.constants.SettingType;
import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.mgr.system.model.SystemSetting;
import com.pr.sepp.sep.build.dao.SonarDAO;
import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.GitProperties;
import com.pr.sepp.sep.build.model.sonar.*;
import com.pr.sepp.sep.build.service.SonarScanService;
import com.pr.sepp.utils.jenkins.JenkinsClient;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import com.pr.sepp.utils.sonar.SonarProperties;
import lombok.extern.slf4j.Slf4j;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabBranch;
import org.gitlab.api.models.GitlabProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.pr.sepp.sep.build.model.constants.InstanceType.ORDINARY;

@Service
@Transactional
@Slf4j
public class SonarScanImpl implements SonarScanService {
	@Autowired
	private SonarDAO sonarDAO;

	@Autowired
	private SettingDAO settingDAO;

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
		project.setInstanceName(sonarScanReq.getInstanceName());
		project.setProjectVersion(sonarScanReq.getGitBranch() + "_" + sonarScanReq.getStartTime());
		for (String projectKey : sonarScanReq.getProjectKeys()) {
			project.setProjectKey(projectKey);
			sonarDAO.insertSonarScan(project);
		}

		HashMap<String, String> paramMap = new HashMap<>();
		paramMap.put("projectName", String.join(",", sonarScanReq.getProjectKeys()));
		paramMap.put("gitBranch", sonarScanReq.getGitBranch());
		paramMap.put("language", sonarScanReq.getLanguage());
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

	@Override
	public boolean handleConfig() throws IOException {
	    List<SonarProperties.SonarConfig> sonarConfigs = SonarProperties.settingToSonarConfig(settingDAO.findSetting(SettingType.SONAR.getValue()));
        System.out.println(sonarConfigs.size());
		if(sonarConfigs.size() > 0){
			if(sonarConfigs.get(0).getBaseHost().length()>0){
				return true;
			}else{
				return false;
			}
		}else {
			return false;
		}
	}

	public List<SonarProjectNames> listSonarProjectNames(Integer noteId) {
		return sonarDAO.listSonarProjectNames(noteId);
	}

	@Override
	public List<SonarScanHistory> sonarAllScanHistory(Integer productId, String projectKey) {
		return sonarDAO.ListAllSonarScanHistory(productId, projectKey);
	}

	@Override
	public List<GitlabBranch> listBranch(BuildInstance buildInstance) throws IOException {
		SystemSetting gitConfig = settingDAO.findSetting(SettingType.GIT.getValue());


		if(gitConfig == null){
			log.info("尚未查询到Git系统配置" );
			return null;
		}
		List<GitProperties.GitConfig> gitConfigs = GitProperties.settingToGitConfig(gitConfig);
		String apiToken = null;
		String repoUrl = null;

		for (GitProperties.GitConfig git : gitConfigs) {
			if (git.getRepoUrl().equals(buildInstance.getRepoUrl())) {
				apiToken = git.getApiToken();
				repoUrl = buildInstance.getRepoUrl();
				break;
			}
		}
		GitlabProject project = GitlabAPI.connect(repoUrl, apiToken).getProject(buildInstance.getNamespace(), buildInstance.getProjectName());
		List<GitlabBranch> ListBraches = GitlabAPI.connect(repoUrl, apiToken).getBranches(project);
		return ListBraches;
	}

	@Override
	public List<SonarReqInstance> listInstance(Integer productId) {
		return sonarDAO.listInstance(productId);
	}
}
