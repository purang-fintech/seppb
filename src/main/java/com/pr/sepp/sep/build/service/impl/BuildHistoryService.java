package com.pr.sepp.sep.build.service.impl;

import com.google.common.collect.Lists;
import com.pr.sepp.common.exception.SeppClientException;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.env.info.dao.EnvInfoDAO;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.sep.build.dao.BuildDAO;
import com.pr.sepp.sep.build.model.BuildFile;
import com.pr.sepp.sep.build.model.BuildHistory;
import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.sep.build.model.resp.BuildHistoryResp;
import com.pr.sepp.utils.jenkins.JenkinsClient;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.pr.sepp.sep.build.model.constants.JenkinsBuildStatus.BUILDING;
import static com.pr.sepp.sep.build.model.resp.BuildHistoryResp.mapToBuildHistoryResps;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Slf4j
@Service
public class BuildHistoryService {


    @Autowired
    private BuildDAO buildDAO;
    @Autowired
    EnvInfoDAO infoDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private JenkinsClientProvider jenkinsClientProvider;


    /**
     * 第一次保存构建信息
     *
     * @param buildHistory
     */
    public Integer save(BuildHistory buildHistory) {
        Integer buildVersion = buildVersionGenerator(buildHistory.getJobName(), buildHistory.getType());
        buildHistory.setBuildVersion(buildVersion);
        buildHistory.setBranchName(nonNull(buildHistory.getBranchName()) ? buildHistory.getBranchName() :
                productDAO.findProductBranch(buildHistory.getBranchId()).getBranchName());
        buildHistory.setSubmitter(userDAO.findUserByUserId(ParameterThreadLocal.getUserId()).getUserName());
        buildDAO.saveBuild(buildHistory);
        return buildVersion;
    }


    /**
     * 当有多个线程同时更新时存在线程安全问题
     *
     * @param jobName
     * @return
     * @throws IOException
     */
    private Integer buildVersionGenerator(String jobName, InstanceType instanceType) {
        try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(instanceType)){
            return jenkinsClient.buildVersionGenerator(jobName, getBuildVersionByJobName(jobName));
        } catch (Exception e) {
            throw new SeppClientException(String.format("获取当前job:%s的构建id失败", jobName), e);
        }
    }


    /**
     * 获取每个功能分支的构建发布历史
     *
     * @param noteId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public List<BuildHistoryResp> buildHistories(Integer noteId, Integer pageNum, Integer pageSize) {
        List<BuildHistory> buildHistories = buildDAO.buildHistories(noteId, (pageNum - 1) * pageSize, pageSize);
        return mapToBuildHistoryResps(buildHistories.stream().map(this::building).collect(groupingBy(BuildHistory::getTag)));
    }

    public List<BuildHistory> buildHistories(String jobName) {
        List<BuildHistory> buildHistories = buildDAO.getBuildHistories(jobName);
        if (isNotEmpty(buildHistories)) {
            return buildHistories.stream().map(this::building).collect(toList());
        }
        return Lists.newArrayList();
    }

    private BuildHistory building(BuildHistory buildHistory) {
        if (buildHistory.getBuildStatus() == BUILDING && Objects.nonNull(buildHistory.getType())) {
            try(JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(buildHistory.getType())) {
                JenkinsClient.BuildProgress buildProgress = jenkinsClient
                        .buildProgress(buildHistory.getJobName(),
                                buildHistory.getBuildVersion());
                if (buildProgress.getExecutor() != null) {
                    buildHistory.getStatus().setPercentage(buildProgress.getExecutor().getProgress());
                }
            } catch (Exception e) {
                log.error("获取{}的构建进度失败:{}", buildHistory.getJobName(), e);
            }
        }
        return buildHistory;
    }

    public Map<String, Object> buildLog(String jobName, Integer buildVersion, InstanceType instanceType) {
        try (JenkinsClient jenkinsClient = jenkinsClientProvider.getJenkinsClient(instanceType)){
            return jenkinsClient.getLog(jobName, buildVersion);
        } catch (Exception e) {
            throw new SeppClientException(String.format("获取%s的构建日志失败", jobName), e);
        }
    }

    public List<BuildInstance> allBuildInstances(Integer productId) {
        return buildDAO.allBuildInstances(productId);
    }

    public Map<String, List<BuildFile>> listBuildFiles(Integer noteId, List<String> instances) {
        List<BuildFile> buildFiles = buildDAO.listBuildFiles(noteId, instances);
        return buildFiles.stream().collect(groupingBy(BuildFile::getInstance));
    }

    public void createOrUpdate(BuildHistory buildHistory) {

        buildDAO.update(buildHistory);
    }

    public Integer getBuildVersionByJobName(String jobName) {
        return buildDAO.maxBuildVersion(jobName);
    }

    public Optional<BuildHistory> findBuildHistoryById(Integer id) {
        return buildDAO.findBuildHistoryById(id);
    }
}
