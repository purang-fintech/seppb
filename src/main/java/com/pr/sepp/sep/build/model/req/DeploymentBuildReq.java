package com.pr.sepp.sep.build.model.req;

import com.google.common.collect.Maps;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.build.model.BuildHistory;
import com.pr.sepp.sep.build.model.DeploymentHistory;
import com.pr.sepp.sep.build.model.constants.BuildType;
import com.pr.sepp.sep.build.model.constants.DeploymentStatus;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.utils.jenkins.model.ParameterDefinition;
import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pr.sepp.common.constants.CommonParameter.*;
import static com.pr.sepp.sep.build.model.constants.JenkinsBuildStatus.START;
import static com.pr.sepp.utils.jenkins.model.ParameterDefinition.parameterDefinitionListToMap;

@Data
public class DeploymentBuildReq {
	@NotEmpty(message = "jobName不能为空")
	private String jobName;
	private String instance;
	private InstanceType instanceType;
	private Integer productId;
	private Integer branchId;
	private Integer envType;
	private String envName;
	private String branchName;
	private BuildType buildType;
	private Integer buildVersion;
	private String deployJobName;
	private Integer deployVersion;

	private List<ParameterDefinition> parameterDefinitions;


	public String parameterDefinitionsToString(String buildType) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> params = parameterDefinitionListToMap(this.parameterDefinitions);
		params.put(BUILD_TYPE, buildType);
		return mapper.writeValueAsString(params);
	}

	public Map<String, String> deployParams() {
		HashMap<String, String> paramsMap = Maps.newHashMap();
		paramsMap.put(BUILD_VERSION, String.valueOf(buildVersion));
		paramsMap.put(JOB_NAME, jobName);
		return paramsMap;
	}

	public DeploymentHistory reqCopyToDeploymentHistory() {
		return DeploymentHistory.builder()
				.branchId(branchId)
				.envType(envType)
				.buildVersion(buildVersion)
				.deployType(buildType == null ? null : buildType.name())
				.jobName(jobName)
				.instance(instance)
				.instanceType(instanceType)
				.deployStatus(DeploymentStatus.DEPLOYING)
				.build();
	}

	public BuildHistory reqCopyToBuildHistory() throws IOException {
		return BuildHistory.builder()
				.jobName(jobName)
				.buildParams(parameterDefinitionsToString(buildType.name()))
				.type(instanceType)
				.branchId(branchId)
				.envType(envType)
				.envName(envName)
				.branchName(branchName)
				.buildStatus(START)
				.productId(ParameterThreadLocal.getProductId())
				.buildType(buildType)
				.build();
	}
}
