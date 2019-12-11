package com.pr.sepp.sep.build.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentBuild {
    private List<BuildHistory> buildHistories;
    private List<DeploymentHistory> deploymentHistories;
    private String deploymentPipelines;

    private Integer currentRunningVersion;
    private Integer currentDeploymentVersion;


    public static String applyDeploymentPipeline(List<DeploymentHistory> deploymentHistories) {
        Optional<DeploymentHistory> deploymentHistoryOptional = deploymentHistories.stream().max(Comparator.comparing(DeploymentHistory::getId));
        DeploymentHistory deploymentHistory = deploymentHistoryOptional.orElseGet(DeploymentHistory::new);
        return deploymentHistory.getPipelineStep();
    }

}
