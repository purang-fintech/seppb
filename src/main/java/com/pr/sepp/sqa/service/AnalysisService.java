package com.pr.sepp.sqa.service;

import java.util.List;
import java.util.Map;

public interface AnalysisService {

	List<Map<String, Object>> defectDirection(Map<String, Object> dataMap);
	
	List<Map<String, Object>> reqType(Map<String, Object> dataMap);

	List<Map<String, Object>> reqPriority(Map<String, Object> dataMap);

	List<Map<String, Object>> reqStatus(Map<String, Object> dataMap);

	List<Map<String, Object>> reqChange(Map<String, Object> dataMap);

	List<Map<String, Object>> reqModule(Map<String, Object> dataMap);

	List<Map<String, Object>> reqClose(Map<String, Object> dataMap);

	List<Map<String, Object>> reqSubmitter(Map<String, Object> dataMap);

	List<Map<String, Object>> reqDevOffset(Map<String, Object> dataMap);

	List<Map<String, Object>> cmsSpliter(Map<String, Object> dataMap);

	List<Map<String, Object>> cmsResponser(Map<String, Object> dataMap);

	List<Map<String, Object>> cmsModule(Map<String, Object> dataMap);

	List<Map<String, Object>> cmsStatus(Map<String, Object> dataMap);

	List<Map<String, Object>> cmsManPower(Map<String, Object> dataMap);

	List<Map<String, Object>> cmsDevOffset(Map<String, Object> dataMap);

	List<Map<String, Object>> tmsSpliter(Map<String, Object> dataMap);

	List<Map<String, Object>> tmsResponser(Map<String, Object> dataMap);

	List<Map<String, Object>> tmsType(Map<String, Object> dataMap);

	List<Map<String, Object>> tmsStatus(Map<String, Object> dataMap);

	List<Map<String, Object>> tmsManPower(Map<String, Object> dataMap);

	List<Map<String, Object>> tmsDevOffset(Map<String, Object> dataMap);

	List<Map<String, Object>> defectFoundDate(Map<String, Object> dataMap);

	List<Map<String, Object>> defectResponseDate(Map<String, Object> dataMap);

	List<Map<String, Object>> defectFixDate(Map<String, Object> dataMap);

	List<Map<String, Object>> defectDeployDate(Map<String, Object> dataMap);

	List<Map<String, Object>> defectVerifyDate(Map<String, Object> dataMap);

	List<Map<String, Object>> defectModule(Map<String, Object> dataMap);

	List<Map<String, Object>> defectReqirements(Map<String, Object> dataMap);

	List<Map<String, Object>> defectFounder(Map<String, Object> dataMap);

	List<Map<String, Object>> defectResponser(Map<String, Object> dataMap);

	List<Map<String, Object>> defectPriority(Map<String, Object> dataMap);

	List<Map<String, Object>> defectInfluence(Map<String, Object> dataMap);

	List<Map<String, Object>> defectType(Map<String, Object> dataMap);

	List<Map<String, Object>> defectFoundPeriod(Map<String, Object> dataMap);

	List<Map<String, Object>> defectProducePeriod(Map<String, Object> dataMap);

	List<Map<String, Object>> defectFixTimes(Map<String, Object> dataMap);

	List<Map<String, Object>> defectResponseCost(Map<String, Object> dataMap);

	List<Map<String, Object>> defectFixCost(Map<String, Object> dataMap);

	List<Map<String, Object>> defectDeployCost(Map<String, Object> dataMap);

	List<Map<String, Object>> defectVerifyCost(Map<String, Object> dataMap);

	List<Map<String, Object>> defectReqDensity(Map<String, Object> dataMap);

	List<Map<String, Object>> defectCmsDensity(Map<String, Object> dataMap);

	List<Map<String, Object>> defectTmsDensity(Map<String, Object> dataMap);

	List<Map<String, Object>> defectManDensity(Map<String, Object> dataMap);

	List<Map<String, Object>> defectCaseDensity(Map<String, Object> dataMap);

	List<Map<String, Object>> defectReqDensityM(Map<String, Object> dataMap);

	List<Map<String, Object>> defectCmsDensityM(Map<String, Object> dataMap);

	List<Map<String, Object>> defectTmsDensityM(Map<String, Object> dataMap);

	List<Map<String, Object>> defectManDensityM(Map<String, Object> dataMap);

	List<Map<String, Object>> problemSubmitter(Map<String, Object> dataMap);

	List<Map<String, Object>> problemResponser(Map<String, Object> dataMap);

	List<Map<String, Object>> problemModule(Map<String, Object> dataMap);

	List<Map<String, Object>> problemStatus(Map<String, Object> dataMap);

	List<Map<String, Object>> problemPriority(Map<String, Object> dataMap);

	List<Map<String, Object>> problemInfluence(Map<String, Object> dataMap);

	List<Map<String, Object>> problemTypeOne(Map<String, Object> dataMap);

	List<Map<String, Object>> problemTypeTwo(Map<String, Object> dataMap);

	List<Map<String, Object>> problemImproveOne(Map<String, Object> dataMap);

	List<Map<String, Object>> problemImproveTwo(Map<String, Object> dataMap);

	List<Map<String, Object>> problemResolveCost(Map<String, Object> dataMap);

	List<Map<String, Object>> problemCloseCost(Map<String, Object> dataMap);
}