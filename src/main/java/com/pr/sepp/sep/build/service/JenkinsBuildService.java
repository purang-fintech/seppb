package com.pr.sepp.sep.build.service;

import com.google.common.collect.Lists;
import com.pr.sepp.common.exception.SeppClientException;
import com.pr.sepp.common.exception.SeppServerException;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.common.websocket.model.DeploymentWebSessionPayload;
import com.pr.sepp.common.websocket.push.DeploymentBuildServer;
import com.pr.sepp.env.info.dao.EnvInfoDAO;
import com.pr.sepp.env.info.model.EnvInfo;
import com.pr.sepp.sep.build.dao.BuildInstanceDAO;
import com.pr.sepp.sep.build.model.BuildFile;
import com.pr.sepp.sep.build.model.BuildHistory;
import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.constants.BuildType;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.sep.build.model.req.BuildHistoryReq;
import com.pr.sepp.sep.build.model.req.DeploymentBuildReq;
import com.pr.sepp.sep.build.service.impl.BuildHistoryService;
import com.pr.sepp.sep.build.service.trigger.JenkinsStatusUpdater;
import com.pr.sepp.utils.jenkins.JenkinsClient;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import com.pr.sepp.utils.jenkins.model.ParameterDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static com.pr.sepp.common.constants.CommonParameter.BUILD_TYPE;
import static com.pr.sepp.common.constants.CommonParameter.DEPLOY_JOB_NAME;
import static com.pr.sepp.sep.build.model.BuildFile.buildFilesToParamsMap;
import static com.pr.sepp.sep.build.model.BuildFile.createDefaultBuildFiles;
import static com.pr.sepp.sep.build.model.constants.JenkinsBuildStatus.START;
import static com.pr.sepp.sep.build.model.req.BuildHistoryReq.listToBuildParams;
import static com.pr.sepp.sep.build.model.req.BuildHistoryReq.mapToBuildParams;
import static com.pr.sepp.utils.jenkins.model.ParameterDefinition.parameterDefinitionListToMap;
import static java.util.Objects.isNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Slf4j
@Service
public class JenkinsBuildService {

	@Autowired
	private EnvInfoDAO envInfoDAO;

	@Autowired
	private BuildHistoryService buildHistoryService;

	@Autowired
	private BuildInstanceDAO buildInstanceDAO;

	@Autowired
	private JenkinsClientProvider jenkinsClientProvider;

	@Autowired
	private DeploymentBuildServer deploymentBuildServer;

	@Autowired
	private DeploymentService deploymentService;

	@Autowired
	private JenkinsStatusUpdater jenkinsStatusUpdater;


	/**
	 * 从数据库获取job所需要的参数</br>
	 * 该参数是在添加实例时所填写的参数
	 *
	 * @param instance
	 * @param productId
	 * @return
	 */
	public List<String> jobParams(String instance, Integer productId) {
		BuildInstance buildInstance = buildInstanceDAO.findInstance(instance, productId);
		String params = buildInstance.getParams();
		if (StringUtils.isNotBlank(params)) {
			return Arrays.asList(params.split(","));
		}
		return Lists.newArrayList();
	}

	/**
	 * 从jenkins获取对应job配置中的所有参数（目前仅支持部分参数类型）
	 * 方便客户端能够从客户端动态的传递job构建时所需要的参数
	 *
	 * @param jobName
	 * @param type
	 * @return
	 */
	public List<ParameterDefinition> getJobBuildParams(String jobName, InstanceType type) {
		try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(type)) {
			return jenkinsClient.jenkinsBuildParams(jobName);
		} catch (Exception e) {
			throw new SeppClientException("无法获取该job的构建参数列表");
		}
	}

	/**
	 * 入口一：通过构建部署页
	 * 该接口支持三种方式</br>
	 * 1.仅构建</br>
	 * 2.构建部署</br>
	 * 3.选择版本部署 文档{@link http://doc.purang.com/docs/sepp/sepp-ci-cd-02}</br>
	 *
	 * @param deploymentBuildReq
	 */
	@Transactional(rollbackFor = Exception.class)
	public void build(DeploymentBuildReq deploymentBuildReq) {
		try {
			List<String> repeatBuildInstance = Lists.newArrayList();
			Map<String, String> params = parameterDefinitionListToMap(deploymentBuildReq.getParameterDefinitions());
			BuildHistory buildHistory = deploymentBuildReq.reqCopyToBuildHistory();
			params.put(BUILD_TYPE, deploymentBuildReq.getBuildType().name());
			buildHistory.setBuildParams(mapToBuildParams(params));
			Integer buildVersion = build(repeatBuildInstance, buildHistory, params);
			if (deploymentBuildReq.getBuildType() == BuildType.BUILD_DEPLOY) {
				deploymentBuildReq.setDeployJobName(DEPLOY_JOB_NAME);
				deploymentBuildReq.setBuildVersion(buildVersion);
				deploymentService.saveDeploymentHistory(deploymentBuildReq);
			}
		} catch (Exception e) {
			throw new SeppServerException("服务器异常，请稍后再试", e);
		}
		deploymentBuildServer.pushByT(DeploymentWebSessionPayload.builder().branchId(deploymentBuildReq.getBranchId())
				.envType(deploymentBuildReq.getEnvType())
				.jobName(deploymentBuildReq.getJobName())
				.build());
	}

	/**
	 * 入口二：发布清单页进行构建部署
	 * 该接口主要为了支持同一需求下，多个job的构建发布
	 *
	 * @param buildHistoryReq
	 */
	public void build(BuildHistoryReq buildHistoryReq) {

		Map<String, List<BuildFile>> instanceBuildFiles = buildHistoryService.listBuildFiles(buildHistoryReq.getNoteId(), buildHistoryReq.getInstances());
		log.debug("添加参数开始：{}", buildHistoryReq.getInstance());
		buildHistoryReq.getInstances().forEach(instance -> {
			if (!buildHistoryReq.getBuildMethod().get(instance)) {
				instanceBuildFiles.put(instance, instanceBuildFiles.get(instance)).get(0).setParamValue(instance);
			}
		});
		log.debug("添加参数结束：{}", buildHistoryReq.getInstance());
		buildHistoryReq.buildInitData();
		build(buildHistoryReq, instanceBuildFiles);
	}

	private void build(BuildHistoryReq buildHistoryReq, Map<String, List<BuildFile>> instanceBuildFiles) {
		List<String> repeatBuildInstance = Lists.newArrayList();
		Set<Map.Entry<String, List<BuildFile>>> entries = instanceBuildFiles.entrySet();
		BuildHistory buildHistory = BuildHistory.reqToBuildHistory(buildHistoryReq);
		for (Map.Entry<String, List<BuildFile>> entry : entries) {
			log.debug("构建历史封装开始{}", buildHistory.getJobName());
			EnvInfo envInfo = envInfoDAO.findEnvInfo(buildHistoryReq.getProductId(), buildHistoryReq.getBranchId(),
					buildHistoryReq.getEnvType(), entry.getKey());
			if (isNull(envInfo)) {
				throw new SeppClientException(String.format("请先到环境配置中配置实例:%s相关信息", entry.getKey()));
			}
			List<BuildFile> buildFiles = entry.getValue();
			//创建job构建所需要的默认参数
			buildFiles.addAll(createDefaultBuildFiles(buildHistoryReq, buildFiles.get(0)));
			//设置job构建历史的必要字段
			setBuildHistory(buildHistory, buildFiles, envInfo);
			//开始构建
			log.debug("构建历史封装完成{}", buildHistory.getJobName());
			build(repeatBuildInstance, buildHistory, buildFilesToParamsMap(buildFiles));
		}
		if (isNotEmpty(repeatBuildInstance)) {
			throw new SeppClientException(String.format("实例%s无法重复构建，请稍后重试", Arrays.toString(repeatBuildInstance.toArray())));
		}
	}

	/**
	 * @param buildHistory
	 * @param buildFiles
	 * @param envInfo
	 */
	private void setBuildHistory(BuildHistory buildHistory,
								 List<BuildFile> buildFiles, EnvInfo envInfo) {
		buildHistory.setInstance(envInfo.getInstance());
		buildHistory.setJobName(envInfo.getJobName());
		buildHistory.setBuildParams(listToBuildParams(buildFiles));
		BuildInstance buildInstance = buildInstanceDAO.findInstance(buildHistory.getInstance(), ParameterThreadLocal.getProductId());
		buildHistory.setType(buildInstance.getType());
	}

	/**
	 * 构建失败重试
	 *
	 * @param id
	 */
	public void retryBuild(Integer id) {
		Optional<BuildHistory> buildHistoryByOptional = buildHistoryService.findBuildHistoryById(id);
		buildHistoryByOptional.ifPresent(this::retryBuild);
	}

	private void retryBuild(BuildHistory buildHistory) {
		List<String> repeatBuildInstance = Lists.newArrayList();
		buildHistory.setBuildStatus(START);
		String buildParams = buildHistory.getBuildParams();
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<BuildFile> buildFiles = mapper.readValue(buildParams, new TypeReference<List<BuildFile>>() {
			});
			BuildInstance buildInstance = buildInstanceDAO.findInstance(buildHistory.getInstance(), ParameterThreadLocal.getProductId());
			buildHistory.setType(buildInstance.getType());
			build(repeatBuildInstance, buildHistory, buildFilesToParamsMap(buildFiles));
		} catch (IOException e) {
			log.error("构建id为:{}的buildParams序列化失败{}", buildHistory.getId(), e);
			throw new SeppServerException(1001, "服务器异常");
		}
	}

	private Integer build(List<String> repeatBuildInstance, BuildHistory buildHistory, Map<String, String> paramsMap) {
		try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(buildHistory.getType())) {
			if (jenkinsClient.checkRepeatBuild(buildHistory.getJobName(), paramsMap)) {
				repeatBuildInstance.add(buildHistory.getJobName());
				return null;
			}
		} catch (Exception e) {
			log.error("校验构建重复性失败:{}", e);
		}
		log.debug("构建开始 {}", buildHistory.getJobName());
		startBuild(buildHistory.getJobName(), paramsMap, buildHistory.getType());
		log.debug("构建结束 {}", buildHistory.getJobName());
		buildHistory.setType(buildHistory.getType());
		Integer buildVersion = buildHistoryService.save(buildHistory);
		jenkinsStatusUpdater.computeIfAbsent(buildHistory.getType(), buildHistory.getJobName());
		return buildVersion;
	}

	private void startBuild(String jobName, Map<String, String> params, InstanceType instanceType) {
		try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(instanceType)) {
			jenkinsClient.startBuild(jobName, params);
		} catch (Exception e) {
			throw new SeppServerException("构建失败，请稍后重试", e);
		}
	}


}
