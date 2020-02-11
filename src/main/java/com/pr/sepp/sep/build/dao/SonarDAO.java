package com.pr.sepp.sep.build.dao;

import com.pr.sepp.sep.build.model.sonar.*;

import java.util.List;

public interface SonarDAO {

	/* 插入扫描任务 */
	void insertSonarScan(SonarScan sonarScan);

	/* 插入扫描结果 */
	int insertSonarResult(SonarResult sonarResult);

	/* 同步成功后修改状态 */
	void syncProjectSuccess(Integer scanId, Integer id);

	/* 需要同步的sonar结果的项目名 */
	List<SonarScan> listUnSyncProject();

	List<SonarScanHistory> ListAllSonarScanHistory(Integer productId, String projectKey);

	/* ReleaseNote对应的项目名称 */
	List<SonarProjectNames> listSonarProjectNames(Integer noteId);

	List<SonarReqInstance> listInstance(Integer productId);

}
