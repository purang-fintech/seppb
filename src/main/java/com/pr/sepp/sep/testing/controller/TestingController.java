package com.pr.sepp.sep.testing.controller;

import com.alibaba.fastjson.JSON;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.sep.testing.model.*;
import com.pr.sepp.sep.testing.service.TestingService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class TestingController {
	@Autowired
	private TestingService testingService;

	@RequestMapping(value = "/case/query", method = RequestMethod.POST)
	public List<CaseFolder> treeQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter("qryProduct"));
		dataMap.put("parentId", request.getParameter("parentId"));
		dataMap.put("isDesc", request.getParameter("isDesc"));
		return testingService.treeQuery(dataMap);
	}

	@RequestMapping(value = "/case/upload", method = RequestMethod.POST)
	public Map<String, Object> caseUpload(@RequestBody String filePath) {
		return testingService.caseUpload(JSON.parseObject(filePath).get("filePath").toString());
	}

	@RequestMapping(value = "/case/save", method = RequestMethod.POST)
	public int caseFolderCreate(@RequestBody CaseFolder caseFolder) {
		return testingService.caseFolderCreate(caseFolder);
	}

	@RequestMapping(value = "/case/paste", method = RequestMethod.POST)
	public int caseFolderPaste(@RequestBody CaseFolder caseFolder) {
		return testingService.caseFolderPaste(caseFolder);
	}

	@RequestMapping(value = "/case/delete/{caseId}", method = RequestMethod.POST)
	public int caseFolderDelete(@PathVariable("caseId") Integer caseId) {
		return testingService.caseFolderDelete(caseId);
	}

	@RequestMapping(value = "/case/update", method = RequestMethod.POST)
	public int caseFolderUpdate(@RequestBody CaseFolder caseFolder) {
		return testingService.caseFolderUpdate(caseFolder);
	}

	@RequestMapping(value = "/case/step_query/{caseId}", method = RequestMethod.POST)
	public List<Map<String, Object>> caseStepQuery(@PathVariable("caseId") Integer caseId) {
		return testingService.caseStepQuery(caseId);
	}

	@RequestMapping(value = "/case/step_save", method = RequestMethod.POST)
	public int caseStepSave(@RequestBody CaseStep caseStep) {
		return testingService.caseStepSave(caseStep);
	}

	@RequestMapping(value = "/case/mind_query/{caseId}", method = RequestMethod.POST)
	public String caseMindQuery(@PathVariable("caseId") Integer caseId) {
		return testingService.caseMindQuery(caseId);
	}

	@RequestMapping(value = "/case/mind_save", method = RequestMethod.POST)
	public int caseMindSave(@RequestBody CaseMind caseMind) {
		return testingService.caseMindSave(caseMind);
	}

	@RequestMapping(value = "/case/step_delete/{caseId}/{stepId}", method = RequestMethod.POST)
	public int caseStepDelete(@PathVariable("caseId") Integer caseId, @PathVariable("stepId") Integer stepId) {
		return testingService.caseStepDelete(caseId, stepId);
	}

	@RequestMapping(value = "/case/step_update", method = RequestMethod.POST)
	public int caseStepUpdate(@RequestBody String steps) {
		Map<String, Object> dataMap = new HashMap<>();
		List<Map<String, Object>> stepData = new Gson().fromJson(JSON.parseObject(steps).get("steps").toString(), new TypeToken<List<Map<String, Object>>>() {
		}.getType());
		dataMap.put("steps", stepData);

		return testingService.caseStepUpdate(dataMap);
	}

	@RequestMapping(value = "/case/info_query/{caseId}", method = RequestMethod.POST)
	public List<CaseInfo> caseInfoQuery(@PathVariable("caseId") Integer caseId) {
		return testingService.caseInfoQuery(caseId);
	}

	@RequestMapping(value = "/case/info_save", method = RequestMethod.POST)
	public int caseInfoSave(@RequestBody CaseInfo caseInfo) throws IllegalAccessException {
		return testingService.caseInfoSave(caseInfo);
	}

	@RequestMapping(value = "/case/info_delete/{caseId}", method = RequestMethod.POST)
	public int caseInfoDelete(@PathVariable("caseId") Integer caseId) {
		return testingService.caseInfoDelete(caseId);
	}

	@RequestMapping(value = "/case/read_query/{caseId}", method = RequestMethod.POST)
	public Map<String, Object> caseReadOnlyQuery(@PathVariable("caseId") Integer caseId) {
		return testingService.caseReadOnlyQuery(caseId);
	}

	@RequestMapping(value = "/case/relate_save", method = RequestMethod.POST)
	public int caseRelateSave(@RequestBody CaseRelationShip caseRelationShip) {
		return testingService.caseRelateSave(caseRelationShip);
	}

	@RequestMapping(value = "/case/relate_delete", method = RequestMethod.POST)
	public int caseRelateDelete(@RequestBody CaseRelationShip caseRelationShip) {
		return testingService.caseRelateDelete(caseRelationShip);
	}

	@RequestMapping(value = "/case/releted_defect/{caseId}", method = RequestMethod.POST)
	public List<Map<String, Object>> relatedDefectQuery(@PathVariable("caseId") Integer caseId) {
		return testingService.relatedDefectQuery(caseId);
	}

	@RequestMapping(value = "/case/releted_req/{caseId}", method = RequestMethod.POST)
	public List<Map<String, Object>> relatedReqQuery(@PathVariable("caseId") Integer caseId) {
		return testingService.relatedReqQuery(caseId);
	}
}
