package com.pr.sepp.sep.build.service;

import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.pr.sepp.common.exception.SeppClientException;
import com.pr.sepp.common.exception.SeppServerException;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.common.websocket.model.DeploymentWebSessionPayload;
import com.pr.sepp.common.websocket.push.DeploymentBuildServer;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.sep.build.dao.DeploymentDAO;
import com.pr.sepp.sep.build.model.DeploymentHistory;
import com.pr.sepp.sep.build.model.constants.DeploymentStatus;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.sep.build.model.req.DeploymentBuildReq;
import com.pr.sepp.sep.build.model.resp.JenkinsBuildResp;
import com.pr.sepp.sep.build.service.trigger.DeploymentStatusUpdater;
import com.pr.sepp.utils.jenkins.JenkinsClient;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.pr.sepp.sep.build.model.constants.DeploymentStatus.RESET;
import static com.pr.sepp.sep.build.service.trigger.DeploymentStatusUpdater.DEPLOYMENT_JOB_NAME;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;


@Slf4j
@Service
public class DeploymentService {

    @Autowired
    private JenkinsClientProvider jenkinsClientProvider;
    @Autowired
    private DeploymentDAO deploymentDAO;
    @Autowired
    private DeploymentBuildServer deploymentBuildServer;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private DeploymentStatusUpdater deploymentStatusUpdater;


    /**
     * 在部署的过程中，需要选择版本号，而目前的部署是采用jenkins去做，而且构建物目前没有统一的存储地址
     * 都是以jenkins为存储仓库，所以获取版本号只用获取对应的jenkins中job的构建成功版本号即可
     *
     * @param jobName
     * @param instanceType
     * @return
     */
    public List<JenkinsBuildResp> selectDeployVersion(String jobName, InstanceType instanceType) {
        try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(instanceType)) {
            List<Build> builds = jenkinsClient.buildsLimit(jobName, 5);
            return builds.stream().map(this::buildJenkinsBuildResp).filter(JenkinsBuildResp::canDeploy).collect(toList());
        } catch (Exception e) {
            throw new SeppClientException(String.format("无法获取%s的可以部署的版本", jobName));
        }
    }

    /**
     * 自动部署接口
     *
     * @param deploymentBuildReq
     */
    @Transactional(rollbackFor = Exception.class)
    public void autoDeploy(DeploymentBuildReq deploymentBuildReq) {
        //检查是否正在部署
        checkJobDeploying(deploymentBuildReq.getJobName(), deploymentBuildReq.getEnvType(), deploymentBuildReq.getBranchId());
        deploymentStatusUpdater.putDeploymentJob(deploymentBuildReq.getJobName(), deploymentBuildReq.getInstanceType());
        String deployJobName = String.format(DEPLOYMENT_JOB_NAME, deploymentBuildReq.getJobName());
        deploymentBuildReq.setDeployJobName(deployJobName);
        try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(deploymentBuildReq.getInstanceType())) {
            //记录部署开始日志
            saveDeploymentHistory(deploymentBuildReq);
            jenkinsClient.startBuild(deployJobName, deploymentBuildReq.deployParams());
            deploymentBuildServer.pushByT(DeploymentWebSessionPayload.builder().branchId(deploymentBuildReq.getBranchId())
                    .envType(deploymentBuildReq.getEnvType())
                    .jobName(deploymentBuildReq.getJobName())
                    .build());
        } catch (Exception e) {
            throw new SeppServerException("部署失败，请稍后再试", e);
        }
    }

    /**
     * 检查该job是否正在部署
     *
     * @param jobName
     * @param envType
     * @param branchId
     * @return
     */
    private boolean checkJobDeploying(String jobName, Integer envType, Integer branchId) {
        List<DeploymentHistory> deploymentHistories = deploymentDAO.selectDeploymentHistories(jobName, envType,
                branchId, DeploymentStatus.DEPLOYING);
        if (CollectionUtils.isNotEmpty(deploymentHistories)) {
            throw new SeppClientException("该实例正在部署，请稍后再试");
        }
        return false;
    }

    /**
     * 记录部署开始的记录（分别往部署历史表和部署过程表中插入数据）
     *
     * @param deploymentBuildReq
     * @throws IOException
     */
    public void saveDeploymentHistory(DeploymentBuildReq deploymentBuildReq) {
        DeploymentHistory deploymentHistory = deploymentBuildReq.reqCopyToDeploymentHistory();
        deploymentHistory.setDeployJobName(deploymentBuildReq.getDeployJobName());
        if (isNull(deploymentBuildReq.getBuildType())) {
            deploymentHistory.setDeployVersion(buildVersionGenerator(deploymentBuildReq.getDeployJobName(), deploymentBuildReq.getInstanceType()));
        }
        deploymentHistory.setUserName(userDAO.findUserByUserId(ParameterThreadLocal.getUserId()).getUserName());
        deploymentDAO.insertDeploymentHistory(deploymentHistory);
    }

    /**
     * 通过jenkins中的版本号和数据库的版本号联合获取准确的下一个构建版本号
     *
     * @param jobName
     * @param instanceType
     * @return
     * @throws IOException
     */
    private synchronized Integer buildVersionGenerator(String jobName, InstanceType instanceType) {
        try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(instanceType)) {
            return jenkinsClient.buildVersionGenerator(jobName, getBuildVersionByJobName(jobName));
        } catch (Exception e) {
            throw new SeppClientException(String.format("获取%s的构建号失败", jobName), e);
        }
    }

    /**
     * 获取数据库中该job的最大构建版本号和jenkins的做对比
     *
     * @param deployJobName
     * @return
     */
    public Integer getBuildVersionByJobName(String deployJobName) {
        return deploymentDAO.maxBuildVersion(deployJobName);
    }

    private JenkinsBuildResp buildJenkinsBuildResp(Build build) {
        BuildWithDetails details;
        try {
            details = build.details();
        } catch (IOException e) {
            throw new SeppClientException("无法该job可以部署的版本");
        }
        return JenkinsBuildResp.apply(build.getNumber(), details.getResult());
    }

    public List<DeploymentHistory> deploymentHistories(String jobName, Integer envType, Integer branchId) {
        return deploymentDAO.deploymentHistories(jobName, envType, branchId);
    }

    public void deploymentStatusReset(DeploymentBuildReq deploymentBuildReq) {
        deploymentDAO.deploymentStatusReset(RESET,
                deploymentBuildReq.getJobName(),
                deploymentBuildReq.getEnvType(),
                deploymentBuildReq.getBranchId());
        deploymentBuildServer.pushByT(DeploymentWebSessionPayload.builder().jobName(deploymentBuildReq.getJobName())
                .envType(deploymentBuildReq.getEnvType())
                .branchId(deploymentBuildReq.getBranchId())
                .build());
    }
}
