package com.pr.sepp.sep.problem.controller;

import com.pr.sepp.base.service.BaseQueryService;
import com.pr.sepp.sep.problem.model.Problem;
import com.pr.sepp.sep.problem.model.ProblemRefuse;
import com.pr.sepp.sep.problem.service.ProblemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class ProblemController {

    @Autowired
    public ProblemService problemService;

    @Autowired
    BaseQueryService baseQueryService;

    @RequestMapping(value = "/problem/query", method = RequestMethod.POST)
    public PageInfo<Problem> reqQuery(HttpServletRequest request) {

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("id", request.getParameter("id"));
        dataMap.put("transId", request.getParameter("transId"));
        dataMap.put("productId", request.getParameter("productId"));
        dataMap.put("submitter", request.getParameter("submitter"));
        dataMap.put("responser", request.getParameter("responser"));
        String status = request.getParameter("status");
        if (!StringUtils.isEmpty(status)) {
            dataMap.put("sts", Arrays.asList(status.split(",")));
        }
        dataMap.put("priority", request.getParameter("priority"));
        dataMap.put("influence", request.getParameter("influence"));
        dataMap.put("moduleId", request.getParameter("moduleId"));
        dataMap.put("typeFirst", request.getParameter("typeFirst"));
        dataMap.put("typeSecond", request.getParameter("typeSecond"));
        if (!StringUtils.isEmpty(request.getParameter("submitTimeBegin"))) {
            dataMap.put("submitTimeBegin", request.getParameter("submitTimeBegin") + " 00:00:00");
        }
        if (!StringUtils.isEmpty(request.getParameter("submitTimeEnd"))) {
            dataMap.put("submitTimeEnd", request.getParameter("submitTimeEnd") + " 23:59:59");
        }
        if (!StringUtils.isEmpty(request.getParameter("resolveTimeBegin"))) {
            dataMap.put("resolveTimeBegin", request.getParameter("resolveTimeBegin") + " 00:00:00");
        }
        if (!StringUtils.isEmpty(request.getParameter("resolveTimeEnd"))) {
            dataMap.put("resolveTimeEnd", request.getParameter("resolveTimeEnd") + " 23:59:59");
        }
        if (!StringUtils.isEmpty(request.getParameter("closeTimeBegin"))) {
            dataMap.put("closeTimeBegin", request.getParameter("closeTimeBegin") + " 00:00:00");
        }
        if (!StringUtils.isEmpty(request.getParameter("closeTimeEnd"))) {
            dataMap.put("closeTimeEnd", request.getParameter("closeTimeEnd") + " 23:59:59");
        }

        int pageNum = StringUtils.isEmpty(request.getParameter("pageNum")) ? 1 : Integer.parseInt(request.getParameter("pageNum"));
        int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 500 : Integer.parseInt(request.getParameter("pageSize"));
        PageHelper.startPage(pageNum, pageSize);

        List<Problem> list = problemService.problemQuery(dataMap);
        PageInfo<Problem> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @RequestMapping(value = "/problem/create", method = RequestMethod.POST)
    public int problemCreate(@RequestBody Problem problem) {
        return problemService.problemCreate(problem);
    }

    @RequestMapping(value = "/problem/update", method = RequestMethod.POST)
    public int problemUpdate(@RequestBody Problem problem) throws IllegalAccessException {
        return problemService.problemUpdate(problem);
    }

    @RequestMapping(value = "/problem/refuse", method = RequestMethod.POST)
    public int problemRefuse(@RequestBody ProblemRefuse problemRefuse) {
        return problemService.problemRefuse(problemRefuse);
    }

}
