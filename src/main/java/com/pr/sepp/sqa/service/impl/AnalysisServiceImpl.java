package com.pr.sepp.sqa.service.impl;

import com.pr.sepp.common.calculation.service.GompertzCalculation;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.product.model.ProductConfig;
import com.pr.sepp.sep.release.dao.ReleaseDAO;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sqa.dao.AnalysisDAO;
import com.pr.sepp.sqa.service.AnalysisService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.pr.sepp.common.constants.CommonParameter.*;

@Transactional
@Service("analysisService")
@Slf4j
public class AnalysisServiceImpl implements AnalysisService {

	@Autowired
	private AnalysisDAO analysisDAO;

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private ReleaseDAO releaseDAO;

	@Autowired
	private GompertzCalculation calService;

	private static final String dateFormat = "yyyy-MM-dd";

	private List<String> getReleaseDates(Map<String, Object> dataMap) {
		String planBegin = String.valueOf(dataMap.get(CommonParameter.QRY_TIME_BEGIN));
		String planEnd = String.valueOf(dataMap.get(CommonParameter.QRY_TIME_END));

		List<String> result = new ArrayList<>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);

		if (null != dataMap.get(REL_ID) && !StringUtils.isEmpty(String.valueOf(dataMap.get(REL_ID)))) {
			Release release = releaseDAO.releaseQuery(dataMap).get(0);
			planBegin = release.getSitBeginDate();
			planEnd = release.getRelDate();
		}

		Date beginDate, endDate;
		try {
			beginDate = format.parse(planBegin);
			endDate = format.parse(planEnd);
			cal.setTime(beginDate);
			while (cal.getTime().before(endDate)) {
				result.add(format.format(cal.getTime()));
				cal.add(Calendar.DATE, 1);
			}
		} catch (ParseException e) {
			log.error("日期解析发生错误，版本计划数据错误", e.getMessage());
			e.printStackTrace();
		}
		result.add(planEnd);

		return result;
	}

	@Override
	public List<Map<String, Object>> defectDirection(Map<String, Object> dataMap) {
		List<String> dates = getReleaseDates(dataMap);
		List<Map<String, Object>> expectTrend = new ArrayList<>();

		if (!StringUtils.isEmpty(MapUtils.getString(dataMap, REL_ID))) {
			Map<String, Object> relMap = new HashMap<>();
			relMap.put(PRODUCT_ID, dataMap.get(PRODUCT_ID));
			relMap.put(REL_ID, dataMap.get(REL_ID));
			Release release = releaseDAO.releaseQuery(relMap).get(0);
			ProductConfig config = productDAO.productConfigQuery(Integer.parseInt((String) dataMap.get(PRODUCT_ID))).get(0);
			Map<String, Double> gompertz = new Gson().fromJson(config.getGompertzParams(), new TypeToken<Map<String, Double>>() {
			}.getType());
			expectTrend = calService.releaseDefectTrend(release, new Double[]{gompertz.get("k"), gompertz.get("a"), gompertz.get("b"), gompertz.get("m")});
		}

		List<Map<String, Object>> defectFound = analysisDAO.defectFoundDate(dataMap);
		List<Map<String, Object>> defectResponse = analysisDAO.defectResponseDate(dataMap);
		List<Map<String, Object>> defectFix = analysisDAO.defectFixDate(dataMap);
		List<Map<String, Object>> defectDeploy = analysisDAO.defectDeployDate(dataMap);
		List<Map<String, Object>> defectVerify = analysisDAO.defectVerifyDate(dataMap);
		List<Map<String, Object>> chartsData = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < dates.size(); i++) {
			Map<String, Object> chartsMap = new HashMap<>();
			String current = dates.get(i);
			chartsMap.put("date", current);
			chartsMap.put("dailyFound", 0);
			chartsMap.put("totalFound", 0);
			chartsMap.put("expectFound", 0);
			chartsMap.put("dailyResponse", 0);
			chartsMap.put("totalResponse", 0);
			chartsMap.put("dailyFixed", 0);
			chartsMap.put("totalFixed", 0);
			chartsMap.put("dailyDeployed", 0);
			chartsMap.put("totalDeployed", 0);
			chartsMap.put("dailyClosed", 0);
			chartsMap.put("totalClosed", 0);

			for (int j = 0; j < defectFound.size(); j++) {
				Map<String, Object> found = defectFound.get(j);
				String foundTime = found.get("foundTime").toString();
				if (foundTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyFound", found.get("dailyFound"));
					chartsMap.put("totalFound", found.get("totalFound"));
					break;
				}
			}

			if (null != dataMap.get(REL_ID)) {
				for (int j = 0; j < expectTrend.size(); j++) {
					Map<String, Object> expect = expectTrend.get(j);
					String summaryDate = expect.get("summaryDate").toString();
					if (summaryDate.equalsIgnoreCase(current)) {
						chartsMap.put("expectFound", expect.get("expectFound"));
						break;
					}
				}
			}

			for (int k = 0; k < defectResponse.size(); k++) {
				Map<String, Object> response = defectResponse.get(k);
				String responseTime = response.get("responseTime").toString();
				if (responseTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyResponse", response.get("dailyResponse"));
					chartsMap.put("totalResponse", response.get("totalResponse"));
					break;
				}
			}
			for (int l = 0; l < defectFix.size(); l++) {
				Map<String, Object> fix = defectFix.get(l);
				String fixedTime = fix.get("fixedTime").toString();
				if (fixedTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyFixed", fix.get("dailyFixed"));
					chartsMap.put("totalFixed", fix.get("totalFixed"));
					break;
				}
			}
			for (int m = 0; m < defectDeploy.size(); m++) {
				Map<String, Object> deploy = defectDeploy.get(m);
				String deployedTime = deploy.get("deployedTime").toString();
				if (deployedTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyDeployed", deploy.get("dailyDeployed"));
					chartsMap.put("totalDeployed", deploy.get("totalDeployed"));
					break;
				}
			}
			for (int m = 0; m < defectVerify.size(); m++) {
				Map<String, Object> close = defectVerify.get(m);
				String closedTime = close.get("closedTime").toString();
				if (closedTime.equalsIgnoreCase(current)) {
					chartsMap.put("dailyClosed", close.get("dailyClosed"));
					chartsMap.put("totalClosed", close.get("totalClosed"));
					break;
				}
			}
			chartsData.add(chartsMap);
		}

		for (int i = 1; i < chartsData.size(); i++) {
			Map<String, Object> chartsMap = chartsData.get(i);
			int expectFound = 0;
			if (null != dataMap.get(REL_ID)) {
				expectFound = Integer.parseInt(chartsMap.get("expectFound").toString());
				expectFound += (expectFound == 0) ? Integer.parseInt(chartsData.get(i - 1).get("expectFound").toString()) : 0;
				chartsMap.put("expectFound", expectFound);
			}

			int totalFound = (int) chartsMap.get("totalFound");
			int totalResponse = (int) chartsMap.get("totalFound");
			int totalFixed = (int) chartsMap.get("totalFixed");
			int totalDeployed = (int) chartsMap.get("totalDeployed");
			int totalClosed = (int) chartsMap.get("totalClosed");

			totalFound += (totalFound == 0) ? (int) chartsData.get(i - 1).get("totalFound") : 0;
			totalResponse += (totalResponse == 0) ? (int) chartsData.get(i - 1).get("totalResponse") : 0;
			totalFixed += (totalFixed == 0) ? (int) chartsData.get(i - 1).get("totalFixed") : 0;
			totalDeployed += (totalDeployed == 0) ? (int) chartsData.get(i - 1).get("totalDeployed") : 0;
			totalClosed += (totalClosed == 0) ? (int) chartsData.get(i - 1).get("totalClosed") : 0;

			chartsMap.put("totalFound", totalFound);
			chartsMap.put("totalResponse", totalResponse);
			chartsMap.put("totalFixed", totalFixed);
			chartsMap.put("totalDeployed", totalDeployed);
			chartsMap.put("totalClosed", totalClosed);
			chartsData.remove(i);
			chartsData.add(i, chartsMap);
		}
		return chartsData;
	}

	@Override
	public List<Map<String, Object>> reqType(Map<String, Object> dataMap) {
		return analysisDAO.reqType(dataMap);
	}

	@Override
	public List<Map<String, Object>> reqPriority(Map<String, Object> dataMap) {
		return analysisDAO.reqPriority(dataMap);
	}

	@Override
	public List<Map<String, Object>> reqStatus(Map<String, Object> dataMap) {
		return analysisDAO.reqStatus(dataMap);
	}

	@Override
	public List<Map<String, Object>> reqChange(Map<String, Object> dataMap) {
		return analysisDAO.reqChange(dataMap);
	}

	@Override
	public List<Map<String, Object>> reqModule(Map<String, Object> dataMap) {
		return analysisDAO.reqModule(dataMap);
	}

	@Override
	public List<Map<String, Object>> reqClose(Map<String, Object> dataMap) {
		return analysisDAO.reqClose(dataMap);
	}

	@Override
	public List<Map<String, Object>> reqSubmitter(Map<String, Object> dataMap) {
		return analysisDAO.reqSubmitter(dataMap);
	}

	@Override
	public List<Map<String, Object>> reqDevOffset(Map<String, Object> dataMap) {
		return analysisDAO.reqDevOffset(dataMap);
	}

	@Override
	public List<Map<String, Object>> cmsSpliter(Map<String, Object> dataMap) {
		return analysisDAO.cmsSpliter(dataMap);
	}

	@Override
	public List<Map<String, Object>> cmsResponser(Map<String, Object> dataMap) {
		return analysisDAO.cmsResponser(dataMap);
	}

	@Override
	public List<Map<String, Object>> cmsManPower(Map<String, Object> dataMap) {
		return analysisDAO.cmsManPower(dataMap);
	}

	@Override
	public List<Map<String, Object>> cmsDevOffset(Map<String, Object> dataMap) {
		return analysisDAO.cmsDevOffset(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectFoundDate(Map<String, Object> dataMap) {
		return analysisDAO.defectFoundDate(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectResponseDate(Map<String, Object> dataMap) {
		return analysisDAO.defectResponseDate(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectFixDate(Map<String, Object> dataMap) {
		return analysisDAO.defectFixDate(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectDeployDate(Map<String, Object> dataMap) {
		return analysisDAO.defectDeployDate(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectVerifyDate(Map<String, Object> dataMap) {
		return analysisDAO.defectVerifyDate(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectModule(Map<String, Object> dataMap) {
		return analysisDAO.defectModule(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectReqirements(Map<String, Object> dataMap) {
		return analysisDAO.defectReqirements(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectFounder(Map<String, Object> dataMap) {
		return analysisDAO.defectFounder(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectResponser(Map<String, Object> dataMap) {
		return analysisDAO.defectResponser(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectPriority(Map<String, Object> dataMap) {
		return analysisDAO.defectPriority(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectInfluence(Map<String, Object> dataMap) {
		return analysisDAO.defectInfluence(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectType(Map<String, Object> dataMap) {
		return analysisDAO.defectType(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectFoundPeriod(Map<String, Object> dataMap) {
		return analysisDAO.defectFoundPeriod(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectProducePeriod(Map<String, Object> dataMap) {
		return analysisDAO.defectProducePeriod(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectFixTimes(Map<String, Object> dataMap) {
		return analysisDAO.defectFixTimes(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectResponseCost(Map<String, Object> dataMap) {
		return analysisDAO.defectResponseCost(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectFixCost(Map<String, Object> dataMap) {
		return analysisDAO.defectFixCost(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectDeployCost(Map<String, Object> dataMap) {
		return analysisDAO.defectDeployCost(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectVerifyCost(Map<String, Object> dataMap) {
		return analysisDAO.defectVerifyCost(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectReqDensity(Map<String, Object> dataMap) {
		return analysisDAO.defectReqDensity(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectCmsDensity(Map<String, Object> dataMap) {
		return analysisDAO.defectCmsDensity(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectTmsDensity(Map<String, Object> dataMap) {
		return analysisDAO.defectTmsDensity(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectManDensity(Map<String, Object> dataMap) {
		return analysisDAO.defectManDensity(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectReqDensityM(Map<String, Object> dataMap) {
		return analysisDAO.defectReqDensityM(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectCmsDensityM(Map<String, Object> dataMap) {
		return analysisDAO.defectCmsDensityM(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectTmsDensityM(Map<String, Object> dataMap) {
		return analysisDAO.defectTmsDensityM(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectManDensityM(Map<String, Object> dataMap) {
		return analysisDAO.defectManDensityM(dataMap);
	}

	@Override
	public List<Map<String, Object>> defectCaseDensity(Map<String, Object> dataMap) {
		List<Map<String, Object>> defectCase = analysisDAO.defectCaseDensity(dataMap);

		defectCase.forEach(item -> {
			String relCaseStr = String.valueOf(item.get("cases"));
			if (null == item.get("cases") || StringUtils.isEmpty(relCaseStr)) {
				item.put("caseNum", 0);
			} else {
				List<String> cases = new ArrayList<>(new HashSet<>(Arrays.asList(relCaseStr.split(","))));
				item.put("caseNum", cases.size());
			}
			item.remove("cases");
		});

		return defectCase;
	}

	@Override
	public List<Map<String, Object>> problemSubmitter(Map<String, Object> dataMap) {
		return analysisDAO.problemSubmitter(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemResponser(Map<String, Object> dataMap) {
		return analysisDAO.problemResponser(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemModule(Map<String, Object> dataMap) {
		return analysisDAO.problemModule(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemStatus(Map<String, Object> dataMap) {
		return analysisDAO.problemStatus(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemPriority(Map<String, Object> dataMap) {
		return analysisDAO.problemPriority(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemInfluence(Map<String, Object> dataMap) {
		return analysisDAO.problemInfluence(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemTypeOne(Map<String, Object> dataMap) {
		return analysisDAO.problemTypeOne(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemTypeTwo(Map<String, Object> dataMap) {
		return analysisDAO.problemTypeTwo(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemImproveOne(Map<String, Object> dataMap) {
		return analysisDAO.problemImproveOne(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemImproveTwo(Map<String, Object> dataMap) {
		return analysisDAO.problemImproveTwo(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemResolveCost(Map<String, Object> dataMap) {
		return analysisDAO.problemResolveCost(dataMap);
	}

	@Override
	public List<Map<String, Object>> problemCloseCost(Map<String, Object> dataMap) {
		return analysisDAO.problemCloseCost(dataMap);
	}

	@Override
	public List<Map<String, Object>> cmsModule(Map<String, Object> dataMap) {
		return analysisDAO.cmsModule(dataMap);
	}

	@Override
	public List<Map<String, Object>> cmsStatus(Map<String, Object> dataMap) {
		return analysisDAO.cmsStatus(dataMap);
	}

	@Override
	public List<Map<String, Object>> tmsSpliter(Map<String, Object> dataMap) {
		return analysisDAO.tmsSpliter(dataMap);
	}

	@Override
	public List<Map<String, Object>> tmsResponser(Map<String, Object> dataMap) {
		return analysisDAO.tmsResponser(dataMap);
	}

	@Override
	public List<Map<String, Object>> tmsType(Map<String, Object> dataMap) {
		return analysisDAO.tmsType(dataMap);
	}

	@Override
	public List<Map<String, Object>> tmsStatus(Map<String, Object> dataMap) {
		return analysisDAO.tmsStatus(dataMap);
	}

	@Override
	public List<Map<String, Object>> tmsManPower(Map<String, Object> dataMap) {
		return analysisDAO.tmsManPower(dataMap);
	}

	@Override
	public List<Map<String, Object>> tmsDevOffset(Map<String, Object> dataMap) {
		return analysisDAO.tmsDevOffset(dataMap);
	}
}