package com.pr.sepp.sep.build.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pr.sepp.sep.build.model.constants.DeploymentStatus;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.offbytwo.jenkins.model.Build;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentHistory {
    private Integer id;
    private String instance;
    private String jobName;
    private Integer envType;
    private Integer branchId;
    private InstanceType instanceType;
    private Integer buildVersion;
    private DeploymentStatus deployStatus;
    private String userName;
    private String deployType;
    private String deployJobName;
    private Integer deployVersion;
    private String pipelineStep;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private DeploymentProcessStatus status;

    public void setDeployStatus(DeploymentStatus deployStatus) {
        this.deployStatus = deployStatus;
        this.status = DeploymentHistory.DeploymentProcessStatus.apply(deployStatus);

    }

    public static DeploymentHistory buildToDeploymentHistory(Build build, String jobName) throws IOException {
        return DeploymentHistory.builder()
                .deployVersion(build.getNumber())
                .deployJobName(jobName)
                .deployStatus(DeploymentStatus.convertJenkinsStatus(build.details()))
                .userName("jenkins")
                .build();
    }

    @Data
    @Builder
    public static class DeploymentProcessStatus {
        private String color;
        private String badge;
        private String value;

        public static DeploymentHistory.DeploymentProcessStatus apply(DeploymentStatus status) {
            if (Objects.isNull(status)) return null;
            return DeploymentHistory.DeploymentProcessStatus.builder().badge(status.badge).color(status.color).value(status.value).build();
        }

    }
}
