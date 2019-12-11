package com.pr.sepp.work.dashboard.controller;

import com.pr.sepp.work.dashboard.service.UserWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@ResponseBody
public class UserWorkController {
    @Autowired
    private UserWorkService userWorkService;

    @RequestMapping(value = "/dashboard/release", method = RequestMethod.POST)
    public Map<String, Object> productReleaseQuery() {
        return userWorkService.productReleaseQuery();
    }

    @RequestMapping(value = "/dashboard/request", method = RequestMethod.POST)
    public Map<String, Object> productRequestQuery() {
        return userWorkService.productRequestQuery();
    }

    @RequestMapping(value = "/dashboard/cms", method = RequestMethod.POST)
    public Map<String, Object> productMissionQuery() {
        return userWorkService.productMissionQuery();
    }

    @RequestMapping(value = "/dashboard/defect", method = RequestMethod.POST)
    public Map<String, Object> productDefectQuery() {
        return userWorkService.productDefectQuery();
    }

    @RequestMapping(value = "/dashboard/testing", method = RequestMethod.POST)
    public Map<String, Object> productTestingQuery() {
        return userWorkService.productTestingQuery();
    }

    @RequestMapping(value = "/dashboard/pipeline", method = RequestMethod.POST)
    public Map<String, Object> productPipelineQuery() {
        return userWorkService.productPipelineQuery();
    }

    @RequestMapping(value = "/dashboard/warning", method = RequestMethod.POST)
    public Map<String, Object> productWarningQuery() {
        return userWorkService.productWarningQuery();
    }

    @RequestMapping(value = "/dashboard/code_healthy", method = RequestMethod.POST)
    public Map<String, Object> productCodeHealthyQuery() {
        return userWorkService.productCodeHealthyQuery();
    }

    @RequestMapping(value = "/dashboard/code_security", method = RequestMethod.POST)
    public Map<String, Object> productCodeSecurityQuery() {
        return userWorkService.productCodeSecurityQuery();
    }

    @RequestMapping(value = "/dashboard/autotest", method = RequestMethod.POST)
    public Map<String, Object> productAutotestQuery() {
        return userWorkService.productAutotestQuery();
    }

    @RequestMapping(value = "/dashboard/coverage", method = RequestMethod.POST)
    public Map<String, Object> productCoverageQuery() {
        return userWorkService.productCoverageQuery();
    }
}
