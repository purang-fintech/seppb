package com.pr.sepp.sep.release.controller;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.release.service.ReleaseService;
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
public class ReleaseController {

	@Autowired
	public ReleaseService releaseService;

	@RequestMapping(value = "/release/create", method = RequestMethod.POST)
	public int releaseCreate(@RequestBody Release release) {
		return releaseService.releaseCreate(release);
	}

	@RequestMapping(value = "/release/update", method = RequestMethod.POST)
	public int releaseUpdate(@RequestBody Release release) throws IllegalAccessException {
		return releaseService.releaseUpdate(release);
	}

	@RequestMapping(value = "/release/query", method = RequestMethod.POST)
	public PageInfo<Release> releaseQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		String status = request.getParameter(CommonParameter.STATUS);
		if (!StringUtils.isEmpty(status)) {
			dataMap.put("sts", Arrays.asList(status.split(",")));
		}
		dataMap.put(CommonParameter.ID, request.getParameter(CommonParameter.ID));
		dataMap.put(CommonParameter.REQ_ID, request.getParameter(CommonParameter.REQ_ID));
		dataMap.put(CommonParameter.BRANCH_ID, request.getParameter(CommonParameter.BRANCH_ID));
		dataMap.put(CommonParameter.CREATOR, request.getParameter(CommonParameter.CREATOR));
		dataMap.put(CommonParameter.RESPONSER, request.getParameter(CommonParameter.RESPONSER));
		dataMap.put(CommonParameter.REL_CODE, request.getParameter(CommonParameter.REL_CODE));
		dataMap.put("relDateBegin", request.getParameter("relDateBegin"));
		dataMap.put("relDateEnd", request.getParameter("relDateEnd"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<Release> list = releaseService.releaseQuery(dataMap);
		PageInfo<Release> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/release/opening", method = RequestMethod.POST)
	public List<Release> openReleaseQuery() {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, String.valueOf(ParameterThreadLocal.getProductId()));
		return releaseService.openReleaseQuery(dataMap);
	}
}
