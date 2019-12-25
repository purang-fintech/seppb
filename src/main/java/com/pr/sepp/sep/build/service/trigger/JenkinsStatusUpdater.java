package com.pr.sepp.sep.build.service.trigger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.pr.sepp.env.info.dao.EnvInfoDAO;
import com.pr.sepp.env.info.model.EnvInfo;
import com.pr.sepp.sep.build.dao.BuildInstanceDAO;
import com.pr.sepp.sep.build.model.BuildHistory;
import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.DeploymentHistory;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.sep.build.service.impl.BuildHistoryService;
import com.pr.sepp.utils.jenkins.JenkinsClient;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import com.pr.sepp.utils.jenkins.model.PipelineStep;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import static com.pr.sepp.common.constants.CommonParameter.DEPLOY_JOB_NAME;
import static com.pr.sepp.sep.build.model.BuildHistory.apply;
import static com.pr.sepp.sep.build.model.constants.DeploymentStatus.convertJenkinsStatus;
import static java.util.stream.Collectors.toList;

@Slf4j
public class JenkinsStatusUpdater implements Updater<JenkinsStatusUpdater> {

    private ConcurrentMap<InstanceType, Set<String>> jobInstancesMap = new ConcurrentHashMap<>();
    private ConcurrentMap<InstanceType, Set<String>> sectionJobsMap = new ConcurrentHashMap<>();
    private JenkinsClientProvider jenkinsClientProvider;
    private BuildHistoryService buildHistoryService;
    private DeploymentStatusUpdater deploymentStatusUpdater;
    private ObjectMapper mapper;
    private EnvInfoDAO envInfoDAO;
    private BuildInstanceDAO buildInstanceDAO;

    public JenkinsStatusUpdater(JenkinsClientProvider jenkinsClientProvider,
                                BuildHistoryService buildHistoryService,
                                DeploymentStatusUpdater deploymentStatusUpdater,
                                ObjectMapper mapper,
                                EnvInfoDAO envInfoDAO,
                                BuildInstanceDAO buildInstanceDAO) {
        this.envInfoDAO = envInfoDAO;
        this.buildInstanceDAO = buildInstanceDAO;
        this.jenkinsClientProvider = jenkinsClientProvider;
        this.buildHistoryService = buildHistoryService;
        this.deploymentStatusUpdater = deploymentStatusUpdater;
        this.mapper = mapper;
    }

    public void computeIfAbsent(InstanceType type, String jobName) {
        Set<String> sectionSets = sectionJobsMap.computeIfAbsent(type, s -> Sets.newConcurrentHashSet());
        sectionSets.add(jobName);
        log.debug("当前被发布过的job集合:{}", sectionJobsMap);
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
        int failedCount = 0;
        JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(instanceType);
        for (String jobName : jobNames) {
            if (failedCount < 5) {
                try {
                    List<Build> builds = jenkinsClient.buildsLimit(jobName, 5);
                    updateBuild(jobName, jenkinsClient, builds);
                } catch (Exception e1) {
                    failedCount++;
                    log.error("更新{}构建结果失败:{}", jobName, e1);
                }
            }
        }
        jenkinsClient.close();

    }

    private void updateBuild(String jobName, JenkinsClient jenkinsClient, List<Build> builds) throws IOException {
        List<Build> buildList = builds.stream().sorted(Comparator.comparing(Build::getNumber).reversed()).limit(5).collect(toList());
        for (Build build : buildList) {
            updateBuild(jobName, jenkinsClient, build);
        }
    }

    private void updateBuild(String jobName, JenkinsClient jenkinsClient, Build build) throws IOException {
        PipelineStep pipelineStep = jenkinsClient.pipelineStep(jobName, build.getNumber());
        BuildWithDetails details = build.details();
        BuildHistory buildHistory = apply(build.getNumber(), details, jobName);
        String pipeline = mapper.writeValueAsString(pipelineStep);
        buildHistory.setPipelineStep(pipeline);
        buildHistoryService.createOrUpdate(buildHistory);
        deploymentStatusUpdater.updateDeploymentResult(DeploymentHistory.builder()
                .jobName(jobName)
                .deployStatus(convertJenkinsStatus(details))
                .buildVersion(build.getNumber())
                .pipelineStep(pipeline)
                .deployJobName(DEPLOY_JOB_NAME).build());
    }

    @PreDestroy
    public void clear() {
        jobInstancesMap.clear();
        sectionJobsMap.clear();
    }

    public void updateWhole() {
        initWholeJobNames();
        jobInstancesMap.forEach(this::updateBuild);
    }

    public void updateSome() {
        sectionJobsMap.forEach(this::updateBuild);
    }

    @Override
    public void update(Consumer<JenkinsStatusUpdater> consumer) {
        consumer.accept(this);
    }
}
