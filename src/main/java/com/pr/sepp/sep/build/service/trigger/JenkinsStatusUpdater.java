package com.pr.sepp.sep.build.service.trigger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pr.sepp.env.info.dao.EnvInfoDAO;
import com.pr.sepp.env.info.model.EnvInfo;
import com.pr.sepp.sep.build.dao.BuildInstanceDAO;
import com.pr.sepp.sep.build.model.BuildHistory;
import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.DeploymentHistory;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.sep.build.service.DeploymentService;
import com.pr.sepp.sep.build.service.impl.BuildHistoryService;
import com.pr.sepp.utils.jenkins.JenkinsClient;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import com.pr.sepp.utils.jenkins.model.PipelineStep;
import com.google.common.collect.Sets;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.pr.sepp.common.constants.CommonParameter.DEPLOY_JOB_NAME;
import static com.pr.sepp.sep.build.model.BuildHistory.apply;
import static com.pr.sepp.sep.build.model.constants.DeploymentStatus.convertJenkinsStatus;

@Slf4j
public class JenkinsStatusUpdater {

    private ConcurrentMap<InstanceType, Set<String>> jobInstancesMap = new ConcurrentHashMap<>();
    private ConcurrentMap<InstanceType, Set<String>> sectionJobsMap = new ConcurrentHashMap<>();
    private JenkinsClientProvider jenkinsClientProvider;
    private BuildHistoryService buildHistoryService;
    private DeploymentService deploymentService;
    private ObjectMapper mapper;
    private EnvInfoDAO envInfoDAO;
    private BuildInstanceDAO buildInstanceDAO;

    public JenkinsStatusUpdater(JenkinsClientProvider jenkinsClientProvider,
                                BuildHistoryService buildHistoryService,
                                DeploymentService deploymentService,
                                ObjectMapper mapper,
                                EnvInfoDAO envInfoDAO,
                                BuildInstanceDAO buildInstanceDAO) {
        this.envInfoDAO = envInfoDAO;
        this.buildInstanceDAO = buildInstanceDAO;
        this.jenkinsClientProvider = jenkinsClientProvider;
        this.buildHistoryService = buildHistoryService;
        this.deploymentService = deploymentService;
        this.mapper = mapper;
    }

    public void updateJobStatus() {
        sectionJobsMap.forEach(this::updateBuild);
    }

    public void updateWholeJobStatus() {
        initWholeJobNames();
        jobInstancesMap.forEach(this::updateBuild);
    }

    public void computeIfAbsent(InstanceType type, String jobName) {
        Set<String> sectionSets = sectionJobsMap.computeIfAbsent(type, s -> Sets.newConcurrentHashSet());
        sectionSets.add(jobName);
        log.debug("当前被发布过的job集合:{}",sectionJobsMap);
    }

    private void initWholeJobNames() {
        try {
            List<EnvInfo> envInfos = envInfoDAO.selectAllJobEnvInfo();
            for (EnvInfo envInfo : envInfos) {
                BuildInstance buildInstance = buildInstanceDAO.findInstance(envInfo.getInstance(), envInfo.getProductId());
                if (Objects.nonNull(buildInstance)) {
                    Set<String> jobSets = jobInstancesMap.computeIfAbsent(buildInstance.getType(), type -> Sets.newConcurrentHashSet());
                    jobSets.add(envInfo.getJobName());
                }
            }
        } catch (Exception e) {
            log.error("获取jobName集合失败：{}", e);
        }
    }

    protected void updateBuild(InstanceType instanceType, Set<String> jobNames) {
        for (String jobName : jobNames) {
            try {
                JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(instanceType);
                List<Build> builds = jenkinsClient.allBuildsByJobName(jobName);
                updateBuild(jobName, jenkinsClient, builds);
            } catch (IOException e) {
                log.error("获取{}的构建结果失败:{}", jobName, e);
            } catch (Exception e1) {
                log.error("更新{}数据库jenkins状态失败:{}", jobName, e1);
            }
        }
    }

    private void updateBuild(String jobName, JenkinsClient jenkinsClient, List<Build> builds) throws IOException {
        for (Build build : builds) {
            PipelineStep pipelineStep = jenkinsClient.pipelineStep(jobName, build.getNumber());
            BuildWithDetails details = build.details();
            BuildHistory buildHistory = apply(build.getNumber(), details, jobName);
            String pipeline = mapper.writeValueAsString(pipelineStep);
            buildHistory.setPipelineStep(pipeline);
            buildHistoryService.createOrUpdate(buildHistory);
            deploymentService.updateDeploymentRsult(DeploymentHistory.builder()
                    .jobName(jobName)
                    .deployStatus(convertJenkinsStatus(details))
                    .buildVersion(build.getNumber())
                    .pipelineStep(pipeline)
                    .deployJobName(DEPLOY_JOB_NAME).build());
        }
    }


}
