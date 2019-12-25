package com.pr.sepp.utils.jenkins;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.client.util.EncodingUtils;
import com.offbytwo.jenkins.client.util.UrlUtils;
import com.offbytwo.jenkins.model.Queue;
import com.offbytwo.jenkins.model.*;
import com.pr.sepp.common.exception.SeppClientException;
import com.pr.sepp.common.exception.SeppServerException;
import com.pr.sepp.utils.jenkins.model.ParameterDefinition;
import com.pr.sepp.utils.jenkins.model.PipelineStep;
import com.pr.sepp.utils.jenkins.pool.JenkinsPoolAbstract;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * jenkins由连接池所创建，每次使用请调用close方法手动关闭，不然会造成连接泄露
 */
@Slf4j
public class JenkinsClient implements Closeable {
    private static final String BUILD_LOG_CSS = "%s<style scoped>.pipeline-new-node {color: #9A9999;}</style>";
    private static final String JENKINS_BUILD_PARAM_CLASS = "hudson.model.ParametersDefinitionProperty";

    private JenkinsServer jenkinsServer;
    private JenkinsHttpClient client;
    private JenkinsPoolAbstract source;
    private boolean broken = false;

    public JenkinsClient(JenkinsServer jenkinsServer, JenkinsHttpClient client) {
        this.jenkinsServer = jenkinsServer;
        this.client = client;
    }

    public JenkinsClient(String url, String username, String password, HttpClientBuilder builder) {
        this.client = new JenkinsHttpClient(URI.create(url), builder, username, password);
        this.jenkinsServer = new JenkinsServer(client);
    }

    public void startBuild(@NonNull String jobName, Map<String, String> params) {
        JobWithDetails job = job(jobName);
        try {
            if (nonNull(job)) job.build(params);
        } catch (IOException e) {
            broken = true;
            throw new SeppServerException("构建失败", e);
        }
    }

    public List<Build> allBuildsByJobName(@NonNull String jobName) {
        JobWithDetails job = job(jobName);
        if (Objects.isNull(job)) return Lists.newArrayList();
        return job.getBuilds();
    }

    public List<Build> buildsLimit(@NonNull String jobName, int limit) {
        if (limit <= 0) throw new SeppServerException(1001, "limit必须大于0");
        return allBuildsByJobName(jobName).stream()
                .sorted(Comparator.comparing(Build::getNumber)
                        .reversed()).limit(limit).collect(toList());
    }

    public Integer lastBuildNumber(@NonNull String jobName) {
        return ofNullable(job(jobName)).map(JobWithDetails::getLastBuild).orElse(new Build()).getNumber();
    }

    public BuildProgress buildProgress(String jobName, Integer version) {
        try {
            return client.get("/job/" + EncodingUtils.encode(jobName) + "/" + version + "?tree=executor[progress]", BuildProgress.class);
        } catch (IOException e) {
            broken = true;
            throw new SeppServerException("获取job构建进度失败", e);
        }
    }

    public Queue queue() throws IOException {
        return jenkinsServer.getQueue();
    }

    public Integer buildVersionGenerator(String jobName, Integer maxBuildVersion) {
        int lastNumber = lastBuildNumber(jobName);
        if (lastNumber <= 0) {
            return 1;
        }
        if (nonNull(maxBuildVersion) && maxBuildVersion >= lastNumber) {
            return maxBuildVersion + 1;
        }
        return lastNumber + 1;
    }

    /**
     * 获取对应job的pipeline构建流水
     *
     * @param jobName
     * @return
     */
    public PipelineStep pipelineStep(@NonNull String jobName, @NonNull Integer version) {
        try {
            return client.get("/job/" + EncodingUtils.encode(jobName) + "/" + version + "/wfapi/describe", PipelineStep.class);
        } catch (IOException e) {
            broken = true;
            throw new SeppServerException("获取jenkins构建流水失败", e);
        }
    }

    public List<String> jobParams(@NonNull String jobName) {
        List<ParameterProperty> property = parameterProperties(jobName);
        if (CollectionUtils.isNotEmpty(property)) {
            Optional<ParameterProperty> first = property.stream().filter(ParameterProperty::allNonNull).findFirst();
            return first.map(ParameterProperty::getParameterDefinitions).orElse(Lists.newArrayList())
                    .stream().map(ParameterDefinition::getName).collect(toList());
        }
        return Lists.newArrayList();
    }

    public List<ParameterProperty> parameterProperties(@NonNull String jobName) {
        try {
            MyJobWithDetails myJobWithDetails = client.get(UrlUtils.toJobBaseUrl(null, jobName), MyJobWithDetails.class);
            return myJobWithDetails.getProperty();
        } catch (IOException e) {
            broken = true;
            throw new SeppServerException("获取参数失败", e);
        }
    }

    public List<ParameterDefinition> jenkinsBuildParams(@NonNull String jobName) {
        Optional<ParameterProperty> parameterPropertyOptional = parameterProperties(jobName).stream()
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
        List<QueueItem> queueItems = new LinkedList<>();
        try {
            queueItems = queueItemsByJobName(this.queue().getItems(), jobName);
        } catch (IOException e) {
            log.warn("check repeat build error:{}", e.getMessage());
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

    @Override
    public void close() {
        if (source != null) {
            JenkinsPoolAbstract jenkinsPoolAbstract = this.source;
            this.source = null;
            if (broken) {
                jenkinsPoolAbstract.returnBrokenResource(this);
            } else {
                jenkinsPoolAbstract.returnResource(this);
            }
        } else {
            disconnect();
        }
    }

    public static void close(JenkinsClient jenkinsClient) {
        if (Objects.nonNull(jenkinsClient)) {
            jenkinsClient.close();
        }
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

    private JobWithDetails job(String jobName) {
        try {
            return jenkinsServer.getJob(jobName);
        } catch (IOException e) {
            broken = true;
            throw new SeppServerException("获取job失败", e);
        }
    }

    public boolean isRunning() {
        return jenkinsServer.isRunning();
    }

    public Map<String, Object> getLog(@NonNull String jobName, @NonNull Integer buildVersion) {
        Map<String, Object> buildLogMap = Maps.newHashMap();
        JobWithDetails job = job(jobName);
        if (job == null) return Maps.newHashMap();
        Build build = job.getBuildByNumber(buildVersion);
        if (Objects.isNull(build)) {
            throw new SeppClientException("无法获取该版本的构建日志");
        }
        try {
            buildLogMap.put("continue", build.details().isBuilding());
            buildLogMap.put("buildLog", String.format(BUILD_LOG_CSS, job.getBuildByNumber(buildVersion).details().getConsoleOutputHtml()));
        } catch (IOException e) {
            broken = true;
            throw new SeppServerException("获取日志信息失败", e);
        }
        return buildLogMap;
    }

    public void setSource(final JenkinsPoolAbstract source) {
        this.source = source;
    }

    public void disconnect() {
        client.close();
        jenkinsServer.close();
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
