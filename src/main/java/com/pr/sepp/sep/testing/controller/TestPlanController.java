package com.pr.sepp.sep.testing.controller;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.testing.model.TestPlan;
import com.pr.sepp.sep.testing.service.TestPlanService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class TestPlanController {
    @Autowired
    private TestPlanService testPlanService;

    @RequestMapping(value = "/plan/query", method = RequestMethod.POST)
    public PageInfo<TestPlan> testPlanQuery(HttpServletRequest request) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
        dataMap.put(CommonParameter.REL_ID, request.getParameter(CommonParameter.REL_ID));
        dataMap.put(CommonParameter.ID, request.getParameter("planId"));
        dataMap.put("planType", request.getParameter("planType"));
        dataMap.put("planStatus", request.getParameter("planStatus"));
        dataMap.put(CommonParameter.RESPONSER, request.getParameter(CommonParameter.RESPONSER));
        if (!StringUtils.isEmpty(request.getParameter("planedDateBegin"))) {
            dataMap.put("planedDateBegin", request.getParameter("planedDateBegin") + " 00:00:00");
        }
        if (!StringUtils.isEmpty(request.getParameter("planedDateEnd"))) {
            dataMap.put("planedDateEnd", request.getParameter("planedDateEnd") + " 23:59:59");
        }

        PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

        List<TestPlan> list = testPlanService.testPlanQuery(dataMap);
        PageInfo<TestPlan> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @RequestMapping(value = "/plan/latest/{planType}", method = RequestMethod.POST)
    public TestPlan latestPlanQuery(@PathVariable("planType") Integer planType) {
        return testPlanService.latestPlanQuery(planType);
    }

    @RequestMapping(value = "/plan/create", method = RequestMethod.POST)
    public int testPlanCreate(@RequestBody TestPlan testPlan) {
        return testPlanService.testPlanCreate(testPlan);
    }

    @RequestMapping(value = "/plan/update", method = RequestMethod.POST)
    public int testPlanUpdate(@RequestBody TestPlan testPlan) throws Exception {
        return testPlanService.testPlanUpdate(testPlan);
    }

    @RequestMapping(value = "/plan/delete/{id}", method = RequestMethod.POST)
    public int testPlanDelete(@PathVariable(CommonParameter.ID) Integer id) {
        return testPlanService.testPlanDelete(id);
    }

    @RequestMapping(value = "/plan/tms_close/{planId}", method = RequestMethod.POST)
    public int planTmsClose(@PathVariable("planId") Integer planId) {
        return testPlanService.planTmsClose(planId);
    }
}