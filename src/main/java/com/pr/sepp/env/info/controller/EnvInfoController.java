package com.pr.sepp.env.info.controller;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.env.info.model.EnvInfo;
import com.pr.sepp.env.info.service.EnvInfoService;
import com.pr.sepp.mgr.product.model.ProductBranch;
import com.pr.sepp.mgr.product.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class EnvInfoController {

	@Autowired
	private EnvInfoService envInfoService;

	@Autowired
	private ProductService productService;

	@GetMapping(value = "/env/env-jobs")
	public Map<String, String> getJobNames(@RequestParam("envType") Integer envType, @RequestParam("branchId") Integer branchId) {
		return envInfoService.getJobNames(envType, branchId);
	}

	@RequestMapping(value = "/env/query", method = RequestMethod.POST)
	public PageInfo<EnvInfo> envInfoQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("branchId", request.getParameter("branchId"));
		dataMap.put("envType", request.getParameter("envType"));
		dataMap.put("instance", request.getParameter("instance"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<EnvInfo> list = envInfoService.envInfoQuery(dataMap);
		PageInfo<EnvInfo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/env/branch_query/{productId}", method = RequestMethod.POST)
	public List<ProductBranch> branchQuery(@PathVariable("productId") Integer productId) {
		Map<String, Object> dataMap = Maps.newHashMap();
		dataMap.put("productId", productId);
		return productService.productBranchQuery(dataMap);
	}

	@RequestMapping(value = "/env/instance_query/{productId}", method = RequestMethod.POST)
	public List<String> instanceQuery(@PathVariable("productId") Integer productId) {
		return envInfoService.instanceQuery(productId);
	}

	@RequestMapping(value = "/env/create", method = RequestMethod.POST)
	public int envInfoCreate(@RequestBody EnvInfo envInfo) {
		return envInfoService.envInfoCreate(envInfo);
	}

	@RequestMapping(value = "/env/update", method = RequestMethod.POST)
	public int envInfoUpdate(@RequestBody EnvInfo envInfo) throws IllegalAccessException {
		return envInfoService.envInfoUpdate(envInfo);
	}

	@RequestMapping(value = "/env/delete/{id}", method = RequestMethod.POST)
	public int envInfoDelete(@PathVariable("id") Integer envId) {
		return envInfoService.envInfoDelete(envId);
	}
}
