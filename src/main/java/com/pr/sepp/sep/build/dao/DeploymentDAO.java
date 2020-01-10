package com.pr.sepp.sep.build.dao;

import com.pr.sepp.sep.build.model.DeploymentHistory;
import com.pr.sepp.sep.build.model.constants.DeploymentStatus;

import java.util.List;

public interface DeploymentDAO {

	void insertDeploymentHistory(DeploymentHistory deploymentHistory);

	Integer maxBuildVersion(String deployJobName);

	void createOrUpdate(DeploymentHistory deploymentHistory);

	List<DeploymentHistory> deploymentHistories(String jobName, Integer envType, Integer branchId);

	void deploymentStatusReset(DeploymentStatus status, String jobName, Integer envType, Integer branchId);

	List<DeploymentHistory> selectDeploymentHistories(String jobName, Integer envType,
													  Integer branchId, DeploymentStatus status);

	void updateDeploymentRsult(DeploymentHistory deploymentHistory);
}
