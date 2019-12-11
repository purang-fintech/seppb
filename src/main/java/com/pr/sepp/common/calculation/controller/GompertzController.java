package com.pr.sepp.common.calculation.controller;

import com.alibaba.fastjson.JSON;
import com.pr.sepp.common.calculation.service.GompertzCalculation;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.release.service.ReleaseService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

	// 前端暂无使用
	@RequestMapping(value = "/quality/trend", method = {RequestMethod.GET, RequestMethod.POST})
	public List<Map<String, Object>> defectTrend(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", ParameterThreadLocal.getProductId());
		dataMap.put("relCode", request.getParameter("relCode"));
		List<Release> releases = releaseService.releaseQuery(dataMap);
		if (null == releases || releases.size() == 0) {
			List<Map<String, Object>> result = new ArrayList<>();
			Map<String, Object> empty = new HashMap<>();
			empty.put("errorMessage", "该版本不存在！");
			result.add(empty);
			return result;
		}

		return calService.releaseDefectTrend(releases.get(0));
	}

	// 前端暂无使用
	@RequestMapping(value = "/quality/trend_similar", method = {RequestMethod.GET, RequestMethod.POST})
	public List<Map<String, Object>> defectTrendSimilar(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", ParameterThreadLocal.getProductId());
		dataMap.put("relCode", request.getParameter("relCode"));
		List<Release> currnetReleases = releaseService.releaseQuery(dataMap);
		if (null == currnetReleases || currnetReleases.size() == 0) {
			List<Map<String, Object>> result = new ArrayList<>();
			Map<String, Object> empty = new HashMap<>();
			empty.put("errorMessage", "目标计算版本不存在！");
			result.add(empty);
			return result;
		}

		Map<String, Object> similarMap = new HashMap<>();
		similarMap.put("productId", ParameterThreadLocal.getProductId());
		similarMap.put("relCode", request.getParameter("similarRelCode"));
		List<Release> similarReleases = releaseService.releaseQuery(similarMap);
		if (null == similarReleases || similarReleases.size() == 0) {
			List<Map<String, Object>> result = new ArrayList<>();
			Map<String, Object> empty = new HashMap<>();
			empty.put("errorMessage", "历史参照版本不存在！");
			result.add(empty);
			return result;
		}

		return calService.releaseDefectTrend(currnetReleases.get(0), similarReleases.get(0));
	}

	@RequestMapping(value = "/quality/ruled_sample_params/{productId}", method = {RequestMethod.GET, RequestMethod.POST})
	public String ruledSampleReleaseParams(@PathVariable("productId") Integer productId) {
		Double[] params = calService.gompertzParamAvg(productId);
		return isEmpty(params) ? "没有符合规则的版本！" : Arrays.deepToString(params);
	}

	@RequestMapping(value = "/quality/ruled_sample_release/{productId}", method = {RequestMethod.GET, RequestMethod.POST})
	public List<Release> rulesFilteredRelease(@PathVariable("productId") Integer productId) {
		return calService.rulesFilteredRelease(productId);
	}

	@RequestMapping(value = "/quality/rules_precalc/{productId}", method = {RequestMethod.GET, RequestMethod.POST})
	public String rulesPreCalculation(@PathVariable("productId") Integer productId, @RequestBody String gompertzDefine) {
		String define = JSON.parseObject(gompertzDefine).get("gompertzDefine").toString();
		Map<String, Integer> gompertz = new Gson().fromJson(define, new TypeToken<Map<String, Integer>>() {}.getType());

		Double[] params = calService.rulesPreCalculation(productId, gompertz);
		return Objects.isNull(params) || isEmpty(params) ? "没有符合规则的版本！" : Arrays.deepToString(params);
	}

	@RequestMapping(value = "/quality/release_params/{productId}/{relId}", method = {RequestMethod.GET, RequestMethod.POST})
	public String gompertzParamCalculation(@PathVariable("productId") Integer productId, @PathVariable("relId") Integer relId) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", productId);
		dataMap.put("id", relId);
		List<Release> releases = releaseService.releaseQuery(dataMap);
		if (null == releases || releases.size() == 0) {
			return "该版本不存在！";
		}

		return Arrays.deepToString(calService.gompertzParamCalculation(releases.get(0)));
	}

	@RequestMapping(value = "/quality/defect_trend_preview/{days}", method = {RequestMethod.GET, RequestMethod.POST})
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
