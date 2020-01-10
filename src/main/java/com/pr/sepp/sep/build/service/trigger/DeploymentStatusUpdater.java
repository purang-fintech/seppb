package com.pr.sepp.sep.build.service.trigger;

import com.google.common.collect.Maps;
import com.offbytwo.jenkins.model.Build;
import com.pr.sepp.sep.build.dao.DeploymentDAO;
import com.pr.sepp.sep.build.model.DeploymentHistory;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.utils.jenkins.JenkinsClient;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import com.pr.sepp.utils.jenkins.model.PipelineStep;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class DeploymentStatusUpdater implements Updater<DeploymentStatusUpdater> {

	public static final String DEPLOYMENT_JOB_NAME = "%s_Deploy";

	private Map<String, InstanceType> deploymentJobMap = Maps.newConcurrentMap();
	private final JenkinsClientProvider jenkinsClientProvider;
	private final DeploymentDAO deploymentDAO;

	public DeploymentStatusUpdater(JenkinsClientProvider jenkinsClientProvider, DeploymentDAO deploymentDAO) {
		this.jenkinsClientProvider = jenkinsClientProvider;
		this.deploymentDAO = deploymentDAO;
	}

	@Override
	public void update(Consumer<DeploymentStatusUpdater> consumer) {
		consumer.accept(this);
	}

	public void updateResult() {
		deploymentJobMap.forEach(this::updateDeploymentResult);
	}

	public void updateDeploymentResult(DeploymentHistory deploymentHistory) {
		deploymentDAO.updateDeploymentRsult(deploymentHistory);
	}

	private void updateDeploymentResult(String jobName, InstanceType instanceType) {
		try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(instanceType)) {
			List<Build> builds = jenkinsClient.buildsLimit(jobName, 5);
			for (Build build : builds) {
				PipelineStep pipelineStep = jenkinsClient.pipelineStep(jobName, build.getNumber());
				DeploymentHistory deploymentHistory = DeploymentHistory.buildToDeploymentHistory(build, jobName);
				ObjectMapper mapper = new ObjectMapper();
				deploymentHistory.setPipelineStep(mapper.writeValueAsString(pipelineStep));
				deploymentDAO.createOrUpdate(deploymentHistory);
			}
		} catch (Exception e) {
			log.error("更新{}部署结果失败:{}", jobName, e);
		}
	}

	public void putDeploymentJob(String jobName, InstanceType instanceType) {
		deploymentJobMap.put(String.format(DEPLOYMENT_JOB_NAME, jobName), instanceType);
	}

	@PreDestroy
	private void clear() {
		deploymentJobMap.clear();
	}
}
