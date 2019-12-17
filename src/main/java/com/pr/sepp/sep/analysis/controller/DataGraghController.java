package com.pr.sepp.sep.analysis.controller;

import java.util.List;
import java.util.Map;

import com.pr.sepp.common.constants.CommonParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pr.sepp.sep.analysis.service.DataGraghService;

@RestController
@ResponseBody
public class DataGraghController {
	@Autowired
	private DataGraghService dataGraghService;

	@RequestMapping(value = "/analysis/defect_direction", method =  RequestMethod.POST)
	public List<Map<String, Object>> defectDirection(@RequestParam(value = CommonParameter.REL_ID, required = true) int relId,
			@RequestParam(value = "planType", required = true) int planType) {
		return dataGraghService.defectDirection(relId, planType);
	}

	@RequestMapping(value = "/analysis/defect_distribution", method =  RequestMethod.POST)
	public Map<String, List<Map<String, Object>>> defectDistribution(@RequestParam(value = CommonParameter.REL_ID, required = true) int relId,
			@RequestParam(value = "planType", required = true) int planType) {
		return dataGraghService.defectDistribution(relId, planType);
	}
}