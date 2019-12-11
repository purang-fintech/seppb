package com.pr.sepp.sep.build.controller;

import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.sep.build.model.req.DeploymentBuildReq;
import com.pr.sepp.sep.build.model.resp.JenkinsBuildResp;
import com.pr.sepp.sep.build.service.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeploymentController {

    @Autowired
    private DeploymentService deploymentService;

    @GetMapping(value = "/deployment/deploy-versions")
    public List<JenkinsBuildResp> deployVersion(@RequestParam("jobName") String jobName,
                                                @RequestParam("instanceType") InstanceType instanceType) {
        return deploymentService.selectDeployVersion(jobName, instanceType);
    }

    @PostMapping(value = "/deployment/auto-deploy")
    public void deployArtifacts(@RequestBody DeploymentBuildReq deploymentBuildReq) {
        deploymentService.autoDeploy(deploymentBuildReq);
    }

    @PostMapping(value = "/deployment/status-reset")
    public void deployStatusReset(@RequestBody DeploymentBuildReq deploymentBuildReq) {
        deploymentService.deploymentStatusReset(deploymentBuildReq);
    }
}
