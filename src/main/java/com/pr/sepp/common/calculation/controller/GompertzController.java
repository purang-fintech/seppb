package com.pr.sepp.common.calculation.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pr.sepp.common.calculation.service.GompertzCalculation;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.release.service.ReleaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

@Slf4j
@RestController
public class GompertzController {

	@Autowired
	private GompertzCalculation calService;

	@Autowired
	public ReleaseService releaseService;

	private final static String ERROR_KEY = "errorMessage";

	// 前端暂无使用
	@PostMapping(value = "/quality/trend")
	public List<Map<String, Object>> defectTrend(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		dataMap.put(CommonParameter.REL_CODE, request.getParameter(CommonParameter.REL_CODE));
		List<Release> releases = releaseService.releaseQuery(dataMap);
		if (null == releases || releases.isEmpty()) {
			List<Map<String, Object>> result = new ArrayList<>();
			Map<String, Object> empty = new HashMap<>();
			empty.put(ERROR_KEY, "该版本不存在！");
			result.add(empty);
			return result;
		}

		return calService.releaseDefectTrend(releases.get(0));
	}

	// 前端暂无使用
	@PostMapping(value = "/quality/trend_similar")
	public List<Map<String, Object>> defectTrendSimilar(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		dataMap.put(CommonParameter.REL_CODE, request.getParameter(CommonParameter.REL_CODE));
		List<Release> currnetReleases = releaseService.releaseQuery(dataMap);
		if (null == currnetReleases || currnetReleases.isEmpty()) {
			List<Map<String, Object>> result = new ArrayList<>();
			Map<String, Object> empty = new HashMap<>();
			empty.put(ERROR_KEY, "目标计算版本不存在！");
			result.add(empty);
			return result;
		}

		Map<String, Object> similarMap = new HashMap<>();
		similarMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		similarMap.put(CommonParameter.REL_CODE, request.getParameter("similarRelCode"));
		List<Release> similarReleases = releaseService.releaseQuery(similarMap);
		if (null == similarReleases || similarReleases.isEmpty()) {
			List<Map<String, Object>> result = new ArrayList<>();
			Map<String, Object> empty = new HashMap<>();
			empty.put(ERROR_KEY, "历史参照版本不存在！");
			result.add(empty);
			return result;
		}

		return calService.releaseDefectTrend(currnetReleases.get(0), similarReleases.get(0));
	}

	@PostMapping(value = "/quality/ruled_sample_params/{productId}")
	public String ruledSampleReleaseParams(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
		Double[] params = calService.gompertzParamAvg(productId);
		return isEmpty(params) ? "没有符合规则的版本！" : Arrays.deepToString(params);
	}

	@PostMapping(value = "/quality/ruled_sample_release/{productId}")
	public List<Release> rulesFilteredRelease(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
		return calService.rulesFilteredRelease(productId);
	}

	@PostMapping(value = "/quality/rules_precalc/{productId}")
	public String rulesPreCalculation(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId, @RequestBody String gompertzDefine) {
		String define = JSON.parseObject(gompertzDefine).get("gompertzDefine").toString();
		Map<String, Integer> gompertz = new Gson().fromJson(define, new TypeToken<Map<String, Integer>>() {}.getType());

		Double[] params = calService.rulesPreCalculation(productId, gompertz);
		return Objects.isNull(params) || isEmpty(params) ? "没有符合规则的版本！" : Arrays.deepToString(params);
	}

	@PostMapping(value = "/quality/release_params/{productId}/{relId}")
	public String gompertzParamCalculation(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId, @PathVariable(CommonParameter.REL_ID) Integer relId) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, productId);
		dataMap.put(CommonParameter.ID, relId);
		List<Release> releases = releaseService.releaseQuery(dataMap);
		if (null == releases || releases.isEmpty()) {
			return "该版本不存在！";
		}

		return Arrays.deepToString(calService.gompertzParamCalculation(releases.get(0)));
	}

	@PostMapping(value = "/quality/defect_trend_preview/{days}")
	public List<Map<String, Object>> defectTrendPreview(@RequestBody String calParams, @PathVariable("days") int days) {
		String paramStr = JSON.parseObject(calParams).get("calParams").toString();
		String[] paramArr = paramStr.split(",");
		if (isEmpty(paramArr) || paramArr.length < 3) {
			return null;
		}
		String k = paramArr[0];
		String a = paramArr[1];
		String b = paramArr[2];
		Double[] params = new Double[]{Double.parseDouble(k), Double.parseDouble(a), Double.parseDouble(b)};

		return calService.defectTrendPreview(params, days);
	}

}
