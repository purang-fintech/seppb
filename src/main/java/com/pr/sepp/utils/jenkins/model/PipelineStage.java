package com.pr.sepp.utils.jenkins.model;

import lombok.Data;

@Data
public class PipelineStage {
    private String id;
    private String name;
    private String execNode;
    private String status;
    private Long startTimeMillis;
    private Long durationMillis;
    private Long pauseDurationMillis;

}
