package com.pr.sepp.utils.jenkins.model;

import com.offbytwo.jenkins.model.BaseModel;
import lombok.Data;

import java.util.List;

@Data
public class PipelineStep extends BaseModel {
    private String id;
    private String name;
    private String status;
    private Long startTimeMillis;
    private Long endTimeMillis;
    private Long durationMillis;
    private Long queueDurationMillis;
    private Long pauseDurationMillis;
    private List<PipelineStage> stages;
}
