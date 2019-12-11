package com.pr.sepp.sep.build.service.deployment;

import com.pr.sepp.sep.build.model.req.DeploymentBuildReq;

public interface DeploymentStrategy {

    void autoDeploy(DeploymentBuildReq deploymentBuildReq);

}
