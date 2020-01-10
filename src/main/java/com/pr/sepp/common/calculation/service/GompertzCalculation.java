package com.pr.sepp.common.calculation.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.product.model.ProductConfig;
import com.pr.sepp.sep.release.dao.ReleaseDAO;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sqa.dao.AnalysisDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class GompertzCalculation {

	@Autowired
	private AnalysisDAO analysisDAO;

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private ReleaseDAO releaseDAO;

	private final static String D_FORMAT = "yyyy-MM-dd";

	/**
	 * 以符合设定规则的所有历史版本作为样本，计算指定版本的缺陷预期趋势
	 *
	 * @param release
	 * @return
	 */
	public List<Map<String, Object>> releaseDefectTrend(Release release) {
		List<Map<String, Object>> defectTrend = new ArrayList<>();      //返回值
		List<String> periods = this.getReleaseDates(release);     //测试周期
		Double ratio;

		final Double[] params = this.gompertzParamAvg(release.getProductId());

		Map<String, Object> releaseManpower = analysisDAO.releaseCmsManpower(release.getId());     //当前版本人力总合，如果尚未纳入任务则按照历史均值计算
		if (Objects.equals(null, releaseManpower) || Objects.equals(null, releaseManpower.get("totalManpower"))) {
			ratio = 0.0;
		} else {
			ratio = (float) releaseManpower.get("totalManpower") / params[3];
		}

		// 计算版本周期内每一天对应预期应发现缺陷数
		for (int i = 0; i < periods.size(); i++) {
			Map<String, Object> current = new HashMap<>();
			// 当前版本人力总和除以历史样本版本的平均人力总和
			current.put("summaryDate", periods.get(i));
			current.put("expectFound", ratio <= 0.0001 ? 0 : Math.round(ratio * Math.exp(params[0] + params[1] * Math.pow(params[2], (double) (i + 1)))));
			defectTrend.add(current);
		}

		return defectTrend;
	}

	/**
	 * 以指定参数计算指定版本的缺陷预期趋势
	 *
	 * @param release
	 * @return
	 */
	public List<Map<String, Object>> releaseDefectTrend(Release release, Double[] params) {
		List<Map<String, Object>> defectTrend = new ArrayList<>();      //返回值
		List<String> periods = this.getReleaseDates(release);     //测试周期
		Double ratio;

		Map<String, Object> releaseManpower = analysisDAO.releaseCmsManpower(release.getId());     //当前版本人力总合，如果尚未纳入任务则按照历史均值计算
		if (Objects.equals(null, releaseManpower) || Objects.equals(null, releaseManpower.get("totalManpower"))) {
			ratio = 0.0;
		} else {
			ratio = (float) releaseManpower.get("totalManpower") / params[3];
		}

		// 计算版本周期内每一天对应预期应发现缺陷数
		for (int i = 0; i < periods.size(); i++) {
			Map<String, Object> current = new HashMap<>();
			// 当前版本人力总和除以历史样本版本的平均人力总和
			current.put("summaryDate", periods.get(i));
			current.put("expectFound", ratio <= 0.0001 ? 0 : Math.round(ratio * Math.exp(params[0] + params[1] * Math.pow(params[2], (double) (i + 1)))));
			defectTrend.add(current);
		}

		return defectTrend;
	}

	/**
	 * 以某个指定版本作为样本，计算指定版本的缺陷预期趋势
	 *
	 * @param currentRelease
	 * @param similarRelease
	 * @return
	 */
	public List<Map<String, Object>> releaseDefectTrend(Release currentRelease, Release similarRelease) {
		final Double[] params = this.gompertzParamCalculation(similarRelease);
		Double ratio;

		List<Map<String, Object>> defectTrend = new ArrayList<>();      //返回值
		List<String> periods = this.getReleaseDates(currentRelease);     //测试周期

		Map<String, Object> currnetRelManpower = analysisDAO.releaseCmsManpower(currentRelease.getId());     //当前版本人力总合，如果尚未纳入任务则按照历史均值计算
		if (Objects.equals(null, currnetRelManpower) || Objects.equals(null, currnetRelManpower.get("totalManpower"))) {
			ratio = 0.0;
		} else {
			Double man = params[3];
			ratio = man < 0.1 ? 0.0 : (float) currnetRelManpower.get("totalManpower") / man;
		}

		// 计算版本周期内每一天对应预期应发现缺陷数
		for (int i = 0; i < periods.size(); i++) {
			Map<String, Object> current = new HashMap<>();
			current.put("summaryDate", periods.get(i));
			current.put("expectFound", ratio <= 0.0001 ? 0 : Math.round(ratio * Math.exp(params[0] + params[1] * Math.pow(params[2], (double) (i + 1)))));
			defectTrend.add(current);
		}

		return defectTrend;
	}

	/**
	 * 根据给定的参数和测试周期，预览理论缺陷发现趋势
	 * @param params
	 * @param days
	 * @return
	 */
	public List<Map<String, Object>> defectTrendPreview(Double[] params, int days) {
		List<Map<String, Object>> defectTrend = new ArrayList<>();      //返回值
		List<String> periods = new ArrayList<>();     //测试周期

		SimpleDateFormat sdf = new SimpleDateFormat(D_FORMAT);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		periods.add(sdf.format(calendar.getTime()));

		for (int i = 0; i < days - 1; i++) {
			calendar.add(Calendar.DATE, 1);
			periods.add(sdf.format(calendar.getTime()));
		}

		// 计算版本周期内每一天对应预期应发现缺陷数
		for (int i = 0; i < periods.size(); i++) {
			Map<String, Object> current = new HashMap<>();
			// 当前版本人力总和除以历史样本版本的平均人力总和
			current.put("summaryDate", periods.get(i));
			current.put("expectFound", Math.round(Math.exp(params[0] + params[1] * Math.pow(params[2], (double) (i + 1)))));
			defectTrend.add(current);
		}

		return defectTrend;
	}

	/**
	 * 对历史所有版本做条件过滤之后进行参数试算
	 *
	 * @param filterdRelease
	 * @return
	 */
	public Double[] ruledReleasesParamCalculation(List<Release> filterdRelease) {
		final Double con = 1.0;

		Map<String, Double> samples = new HashMap<>();
		samples.put("S1", 0.0);
		samples.put("S2", 0.0);
		samples.put("S3", 0.0);
		samples.put("m", 0.0);
		samples.put("man", 0.0);

		filterdRelease.forEach(rel -> {
			Map<String, Object> releaseCmsMan = analysisDAO.releaseCmsManpower(rel.getId());
			samples.put("man", samples.get("man") + (float) releaseCmsMan.get("totalManpower"));

			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put(CommonParameter.REL_ID, rel.getId());
			List<Map<String, Object>> defectFound = analysisDAO.defectFoundDate(dataMap);

			int divided = (int) (defectFound.size() / 3);
			for (int i = 0; i < divided * 3; i++) {
				Object totalFound = defectFound.get(i).get("totalFound");
				if (i < divided) {
					samples.put("S1", samples.get("S1") + Math.log(con * (int) totalFound));
					samples.put("m", samples.get("m") + 1);
				}
				if (i >= divided && i < divided * 2) {
					samples.put("S2", samples.get("S2") + Math.log(con * (int) totalFound));
				}
				if (i >= divided * 2 && i < divided * 3) {
					samples.put("S3", samples.get("S3") + Math.log(con * (int) totalFound));
				}
			}
		});

		Double[] params = this.gompertzParamCalculation(new Double[]{samples.get("S1"), samples.get("S2"), samples.get("S3"), samples.get("m")});

		return new Double[]{params[0], params[1], params[2], samples.get("man") / filterdRelease.size()};
	}

	/**
	 * 对历史所有版本做条件过滤之后进行参数试算
	 *
	 * @param productId
	 * @return
	 */
	public Double[] rulesPreCalculation(int productId, Map<String, Integer> gompertz) {
		List<Release> filterdRelease = this.rulesFilteredRelease(productId, gompertz);
		if (filterdRelease.size() == 0) {
			return null;
		}

		return this.ruledReleasesParamCalculation(filterdRelease);
	}

	/**
	 * 对历史所有版本做条件过滤之后进行参数计算
	 *
	 * @param productId
	 * @return
	 */
	public Double[] gompertzParamAvg(int productId) {
		List<Release> filterdRelease = this.rulesFilteredRelease(productId);
		if (filterdRelease.size() == 0) {
			return null;
		}

		return this.ruledReleasesParamCalculation(filterdRelease);
	}

	/**
	 * 基于公式为 y = e^(k+ab^t) 的Gompertz模型参数计算，输入的S1、S2、S3为Σln(y)1、Σln(y)2、Σln(y)3，m为采样点个数
	 *
	 * @param params
	 * @return
	 */
	public Double[] gompertzParamCalculation(Double[] params) {
		Double S1 = params[0];
		Double S2 = params[1];
		Double S3 = params[2];
		Double m = params[3];
		final Double ratio = 1.0;

		// 参数k的计算
		Double k = (1 / m) * (S3 * S1 - Math.pow(S2, 2)) / (S3 + S1 - 2 * S2);
		Double b = Math.pow((S3 - S2) / (S2 - S1), ratio / m);
		Double a = (b - 1) / (b * Math.pow((Math.pow(b, m) - 1), 2)) * (S2 - S1);

		return new Double[]{k, a, b, m};
	}

	/**
	 * 基于公式为 y = e^(k+ab^t) 的Gompertz模型参数计算
	 *
	 * @param release
	 * @return
	 */
	public Double[] gompertzParamCalculation(Release release) {
		Map<String, Object> releaseCmsMan = analysisDAO.releaseCmsManpower(release.getId());

		Double[] lnedCurve = this.defectFoundDevide(release);
		Double S1 = lnedCurve[0];
		Double S2 = lnedCurve[1];
		Double S3 = lnedCurve[2];
		Double m = lnedCurve[3];
		final Double ratio = 1.0;

		// 参数k的计算
		Double k = (1 / m) * (S3 * S1 - Math.pow(S2, 2)) / (S3 + S1 - 2 * S2);
		Double b = Math.pow((S3 - S2) / (S2 - S1), ratio / m);
		Double a = (b - 1) / (b * Math.pow((Math.pow(b, m) - 1), 2)) * (S2 - S1);

		return new Double[]{k, a, b, ratio * (float) releaseCmsMan.get("totalManpower")};
	}

	/**
	 * 返回Σln(y)1、Σln(y)2、Σln(y)3，以及每个采样区间的采样点个数
	 *
	 * @param release
	 * @return
	 */
	private Double[] defectFoundDevide(Release release) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.REL_ID, release.getId());
		List<Map<String, Object>> defectFound = analysisDAO.defectFoundDate(dataMap);

		BigDecimal bd = new BigDecimal(String.valueOf(defectFound.size() / 3));
		double dataCount = bd.doubleValue();
		int divided = (int) dataCount;
		double S1 = 0.0;
		double S2 = 0.0;
		double S3 = 0.0;
		final double con = 1.0;
		for (int i = 0; i < divided * 3; i++) {
			Object totalFound = defectFound.get(i).get("totalFound");
			if (i < divided) {
				S1 += Math.log(con * (int) totalFound);
			}
			if (i >= divided && i < divided * 2) {
				S2 += Math.log(con * (int) totalFound);
			}
			if (i >= divided * 2 && i < divided * 3) {
				S3 += Math.log(con * (int) totalFound);
			}
		}
		return new Double[]{S1, S2, S3, dataCount};
	}

	/**
	 * 根据产品高级配置信息，读取度量参考的版本特征规则
	 *
	 * @param productId
	 * @return
	 */
	public List<Release> rulesFilteredRelease(int productId) {
		List<Release> sampleReleases = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat(D_FORMAT);
		Calendar calendar = Calendar.getInstance();

		//产品配置数据
		ProductConfig config = productDAO.productConfigQuery(productId).get(0);
		Map<String, Integer> gompertz = new Gson().fromJson(config.getGompertzDefine(), new TypeToken<Map<String, Integer>>() {
		}.getType());

		// 查询该产品下所有符合条件的历史版本（满足最近发布日期在N个月之内，N是产品高级配置）
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1 * gompertz.get("latestOffsetMonth"));
		Map<String, Object> qryMap = new HashMap<>();
		qryMap.put(CommonParameter.PRODUCT_ID, productId);
		qryMap.put("relDateBegin", sdf.format(calendar.getTime()));
		List<Release> releaseList = releaseDAO.releaseQuery(qryMap);

		releaseList.forEach(rel -> {
			// 尚未关闭的版本排除
			if (rel.getStatus() > 0) {
				return;
			}

			// 测试周期小于指定天数的版本排除
			if (this.getReleaseDates(rel).size() < gompertz.get("minTestPeriod")) {
				return;
			}

			// 测试缺陷小于指定数量的版本排除
			Map<String, Object> relDefect = analysisDAO.releaseDefectCount(rel.getId());
			if (null == relDefect || (int) relDefect.get("defectCount") < gompertz.get("minDefectCount")) {
				return;
			}

			Map<String, Object> releaseCmsMan = analysisDAO.releaseCmsManpower(rel.getId());
			if (null == releaseCmsMan) {
				return;
			}

			// 未纳入开发任务的版本数据排除
			if ((int) releaseCmsMan.get("cmsCount") == 0) {
				return;
			}

			// 人力合计为零（或近乎为零）的版本数据排除
			if ((float) releaseCmsMan.get("totalManpower") < 0.1) {
				return;
			}

			sampleReleases.add(rel);
		});

		return sampleReleases;
	}

	/**
	 * 根据产品高级配置信息，读取度量参考的版本特征规则，前端试算用
	 *
	 * @param productId
	 * @return
	 */
	public List<Release> rulesFilteredRelease(int productId, Map<String, Integer> gompertz) {
		List<Release> sampleReleases = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat(D_FORMAT);
		Calendar calendar = Calendar.getInstance();

		// 查询该产品下所有符合条件的历史版本（满足最近发布日期在N个月之内，N是产品高级配置）
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1 * gompertz.get("latestOffsetMonth"));
		Map<String, Object> qryMap = new HashMap<>();
		qryMap.put(CommonParameter.PRODUCT_ID, productId);
		qryMap.put("relDateBegin", sdf.format(calendar.getTime()));
		List<Release> releaseList = releaseDAO.releaseQuery(qryMap);

		releaseList.forEach(rel -> {
			// 尚未关闭的版本排除
			if (rel.getStatus() > 0) {
				return;
			}

			// 测试周期小于指定天数的版本排除
			if (this.getReleaseDates(rel).size() < gompertz.get("minTestPeriod")) {
				return;
			}

			// 测试缺陷小于指定数量的版本排除
			Map<String, Object> relDefect = analysisDAO.releaseDefectCount(rel.getId());
			if (null == relDefect || (int) relDefect.get("defectCount") < gompertz.get("minDefectCount")) {
				return;
			}

			Map<String, Object> releaseCmsMan = analysisDAO.releaseCmsManpower(rel.getId());
			if (null == releaseCmsMan) {
				return;
			}

			// 未纳入开发任务的版本数据排除
			if ((int) releaseCmsMan.get("cmsCount") == 0) {
				return;
			}

			// 人力合计为零（或近乎为零）的版本数据排除
			if ((float) releaseCmsMan.get("totalManpower") < 0.1) {
				return;
			}

			sampleReleases.add(rel);
		});

		return sampleReleases;
	}

	/**
	 * 统计版本的测试周期
	 *
	 * @param release
	 * @return
	 */
	private List<String> getReleaseDates(Release release) {
		List<String> result = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(D_FORMAT);

		Date beginDate, endDate;
		try {
			beginDate = format.parse(release.getSitBeginDate());
			endDate = format.parse(release.getRelDate());
			cal.setTime(beginDate);
			while (cal.getTime().before(endDate)) {
				result.add(format.format(cal.getTime()));
				cal.add(Calendar.DATE, 1);
			}
		} catch (ParseException e) {
			log.error("日期解析发生错误，版本计划数据错误：{}", e.getMessage());
			e.printStackTrace();
		}
		result.add(release.getRelDate());

		return result;
	}
}
