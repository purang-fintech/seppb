package com.pr.sepp.sep.problem.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.base.service.BaseQueryService;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.problem.model.Problem;
import com.pr.sepp.sep.problem.model.ProblemRefuse;
import com.pr.sepp.sep.problem.service.ProblemService;
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
		dataMap.put(CommonParameter.ID, request.getParameter(CommonParameter.ID));
		dataMap.put("transId", request.getParameter("transId"));
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.SUBMITTER, request.getParameter(CommonParameter.SUBMITTER));
		dataMap.put(CommonParameter.RESPONSER, request.getParameter(CommonParameter.RESPONSER));
		String status = request.getParameter(CommonParameter.STATUS);
		if (!StringUtils.isEmpty(status)) {
			dataMap.put("sts", Arrays.asList(status.split(",")));
		}
		dataMap.put("priority", request.getParameter("priority"));
		dataMap.put("influence", request.getParameter("influence"));
		dataMap.put(CommonParameter.MODULE_ID, request.getParameter(CommonParameter.MODULE_ID));
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

		int pageNum = ParameterThreadLocal.getPageNum();
		int pageSize = ParameterThreadLocal.getPageSize();
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
