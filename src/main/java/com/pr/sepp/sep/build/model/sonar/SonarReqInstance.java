package com.pr.sepp.sep.build.model.sonar;


import lombok.Data;

@Data
public class SonarReqInstance {
    private String instance;
    private String projectName;
    private String repoUrl;
    private String namespace;
    private String description;

}
