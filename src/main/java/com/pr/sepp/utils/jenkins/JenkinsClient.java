package com.pr.sepp.utils.jenkins;

import com.pr.sepp.common.exception.SeppClientException;
import com.pr.sepp.utils.jenkins.model.ParameterDefinition;
import com.pr.sepp.utils.jenkins.model.PipelineStep;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.client.util.EncodingUtils;
import com.offbytwo.jenkins.client.util.UrlUtils;
import com.offbytwo.jenkins.model.Queue;
import com.offbytwo.jenkins.model.*;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Slf4j
public class JenkinsClient {
    private static final String BUILD_LOG_CSS = "%s<style scoped>.pipeline-new-node {color: #9A9999;}</style>";
    private static final String JENKINS_BUILD_PARAM_CLASS = "hudson.model.ParametersDefinitionProperty";

    private JenkinsServer jenkinsServer;
    private JenkinsHttpClient client;

    public JenkinsClient(JenkinsServer jenkinsServer, JenkinsHttpClient client) {
        this.jenkinsServer = jenkinsServer;
        this.client = client;
    }

    public void startBuild(@NonNull String jobName, Map<String, String> params) throws IOException {
        JobWithDetails job = job(jobName);
        if (nonNull(job)) job.build(params);
    }

    public List<Build> allBuildsByJobName(@NonNull String jobName) throws IOException {
        JobWithDetails job = job(jobName);
        if (Objects.isNull(job)) return Lists.newArrayList();
        return job.getBuilds();
    }

    public Integer lastBuildNumber(@NonNull String jobName) throws IOException {
        return ofNullable(job(jobName)).map(JobWithDetails::getLastBuild).orElse(new Build()).getNumber();
    }

    public BuildProgress buildProgress(String jobName, Integer version) throws IOException {
        return client.get("/job/" + EncodingUtils.encode(jobName) + "/" + version + "?tree=executor[progress]", BuildProgress.class);
    }

    public Queue queue() throws IOException {
        return jenkinsServer.getQueue();
    }

    public Integer buildVersionGenerator(String jobName, Integer maxBuildVersion) throws IOException {
        int lastNumber = lastBuildNumber(jobName);
        if (lastNumber <= 0) {
            return 1;
        }
        if (nonNull(maxBuildVersion) && maxBuildVersion >= lastNumber) {
            return maxBuildVersion + 1;
        }
        //todo 更新数据库(更新策略有问题，需要调整)
        return lastNumber + 1;
    }

    /**
     * 获取对应job的pipeline构建流水
     *
     * @param jobName
     * @return
     */
    public PipelineStep pipelineStep(@NonNull String jobName, @NonNull Integer version) throws IOException {
        return client.get("/job/" + EncodingUtils.encode(jobName) + "/" + version + "/wfapi/describe", PipelineStep.class);
    }

    public List<String> jobParams(@NonNull String jobName) throws IOException {
        List<ParameterProperty> property = parameterPropertys(jobName);
        if (CollectionUtils.isNotEmpty(property)) {
            Optional<ParameterProperty> first = property.stream().filter(ParameterProperty::allNonNull).findFirst();
            return first.map(ParameterProperty::getParameterDefinitions).orElse(Lists.newArrayList())
                    .stream().map(ParameterDefinition::getName).collect(toList());
        }
        return Lists.newArrayList();
    }

    public List<ParameterProperty> parameterPropertys(@NonNull String jobName) throws IOException {
        MyJobWithDetails myJobWithDetails = client.get(UrlUtils.toJobBaseUrl(null, jobName), MyJobWithDetails.class);
        return myJobWithDetails.getProperty();
    }

    public List<ParameterDefinition> jenkinsBuildParams(@NonNull String jobName) throws IOException {
        Optional<ParameterProperty> parameterPropertyOptional = parameterPropertys(jobName).stream()
                .filter(details -> JENKINS_BUILD_PARAM_CLASS.equalsIgnoreCase(details.get_class()))
                .findFirst();
        return parameterPropertyOptional.map(ParameterProperty::getParameterDefinitions).orElse(Lists.newArrayList());
    }

    /**
     * 检查job是否重复构建
     * 条件：当同一个job的构建参数及构建参数内容一样时为重复构建
     *
     * @param paramsMap
     * @return
     */
    public boolean checkRepeatBuild(@NonNull String jobName, Map<String, String> paramsMap) {
        List<QueueItem> queueItems = null;
        try {
            queueItems = queueItemsByJobName(this.queue().getItems(), jobName);
        } catch (IOException e) {
        }
        Set<Map.Entry<String, String>> entries = paramsMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            if (checkRepeatBuild(queueItems, entry.getKey(), entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRepeatBuild(List<QueueItem> items, String paramKey, String paramValue) {
        String param = paramKey + "=" + paramValue;
        if (Objects.equals(items.size(), 0)) return false;
        return items.stream().anyMatch(item -> item.getParams().contains(param));
    }

    /**
     * 获取对应job的构建排队队列
     *
     * @param items
     * @param jobName
     * @return
     */
    public List<QueueItem> queueItemsByJobName(List<QueueItem> items, @NonNull String jobName) {
        return items.stream()
                .filter(item -> jobName.equalsIgnoreCase(item.getTask().getName()))
                .collect(toList());
    }

    private JobWithDetails job(String jobName) throws IOException {
        return jenkinsServer.getJob(jobName);
    }

    public Map<String, Object> getLog(@NonNull String jobName, @NonNull Integer buildVersion) throws IOException {
        Map<String, Object> buildLogMap = Maps.newHashMap();
        Build build = job(jobName).getBuildByNumber(buildVersion);
        if (Objects.isNull(build)) {
            throw new SeppClientException("无法获取该版本的构建日志");
        }
        buildLogMap.put("continue", build.details().isBuilding());
        buildLogMap.put("buildLog", String.format(BUILD_LOG_CSS, job(jobName).getBuildByNumber(buildVersion).details().getConsoleOutputHtml()));
        return buildLogMap;
    }

    @Data
    public static class BuildProgress extends BaseModel {

        private ProgressValue executor;

        @Data
        public static class ProgressValue {
            private Integer progress;
        }
    }

    @Data
    public static class MyJobWithDetails extends JobWithDetails {
        private List<ParameterProperty> property;
    }

    @Data
    public static class ParameterProperty extends BaseModel {
        private List<ParameterDefinition> parameterDefinitions;

        public static boolean allNonNull(ParameterProperty property) {
            return nonNull(property) && nonNull(property.getParameterDefinitions());
        }
    }
}
