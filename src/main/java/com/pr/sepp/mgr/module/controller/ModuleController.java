package com.pr.sepp.mgr.module.controller;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.module.model.Module;
import com.pr.sepp.mgr.module.service.ModuleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class ModuleController {

	@Autowired
	private ModuleService moduleService;

	@RequestMapping(value = "/module/query", method = RequestMethod.POST)
	public PageInfo<Module> moduleQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("moduleId", request.getParameter("moduleId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("moduleName", request.getParameter("moduleName"));
		dataMap.put("isValid", request.getParameter("isValid"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<Module> list = moduleService.moduleQuery(dataMap);
		PageInfo<Module> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/module/create", method = RequestMethod.POST)
	public int moduleCreate(@RequestBody Module module) {
		return moduleService.moduleCreate(module);
	}

	@RequestMapping(value = "/module/update", method = RequestMethod.POST)
	public int moduleUpdate(@RequestBody Module module) throws IllegalAccessException {
		return moduleService.moduleUpdate(module);
	}

	@RequestMapping(value = "/module/delete/{moduleId}", method = RequestMethod.POST)
	public int moduleDelete(@PathVariable("moduleId") Integer moduleId) {
		return moduleService.moduleDelete(moduleId);
	}
}
