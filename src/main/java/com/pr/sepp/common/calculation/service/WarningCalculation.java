package com.pr.sepp.common.calculation.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pr.sepp.common.calculation.helper.WarningRuleHelper;
import com.pr.sepp.common.calculation.model.ReleaseSepData;
import com.pr.sepp.common.calculation.model.WarningRules;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.constants.ObjectType;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.mgr.product.model.ProductConfig;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.notify.warning.model.Warning;
import com.pr.sepp.notify.warning.model.WarningBatch;
import com.pr.sepp.notify.warning.service.WarningService;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.testing.dao.TestPlanDAO;
import com.pr.sepp.sep.testing.dao.TestReportDAO;
import com.pr.sepp.sep.testing.model.TestPlan;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 针对打开过程质量预警开关的所有产品下的未关闭版本，进行基于研发过程数据分析的告警计算
 * 告警规则配置参考sepp_warning_rules表expression字段
 * 由于告警计算实时性要求不高，而计算过程要求不能意外中断，故而牺牲性能，将异常捕获放在
 * 多重循环的最内部，发生异常则写日志，继续下一个规则或版本、产品的计算
 */

@Slf4j
@Service
@Transactional
public class WarningCalculation {

	@Autowired
	private GompertzCalculation trendCalculation;

	@Autowired
	private WarningService warningService;

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private TestReportDAO testReportDAO;

	@Autowired
	private TestPlanDAO testPlanDAO;

	@Autowired
	private WarningRuleHelper warningRuleHelper;

	// Aviator 并不支持日期类型，如果要比较日期，你需要将日期写字符串的形式，并且要求是形如 “yyyy-MM-dd HH:mm:ss:SS”的字符串，否则都将报错。
	private final static String DATE_FMT = "yyyy-MM-dd HH:mm:ss:SS";
	private final static String DATE_FMT_S = "yyyy-MM-dd";

	/**
	 * 全量分析计算服务
	 */
	public void warningCalculation() {
		long batchNo = System.currentTimeMillis();

		// 打开过程质量预警开关的所有产品
		List<Product> products = warningService.warningProducts();

		//所有的告警规则集
		List<WarningRules> warningRules = warningService.warningRules();

		// 环境监控类告警暂未实现，需要cmdb采集数据
		// List<WarningRules> envRules = warningRules.stream().filter(f -> f.getTargetType() - 14 == 0).collect(Collectors.toList());

		//版本数据类告警规则集
		List<WarningRules> dataRules = warningRules.stream().filter(f -> f.getTargetType() - 14 != 0).collect(Collectors.toList());

		products.forEach(product -> {
			// 产品下所有未关闭的版本
			List<Release> releases = warningService.productOpenRelease(product.getProductId());
			for (Release release : releases) {
				try {
					releaseWarningCalculation(release, dataRules, batchNo);
				} catch (Exception e) {
					log.warn("版本【{}】告警计算发生异常", release.getRelCode(), e);
					continue;
				}
			}
		});
	}

	/**
	 * 实时计算接口调用服务——按产品计算
	 *
	 * @param productId
	 */
	public void productWarningCalculation(Integer productId) {
		long batchNo = System.currentTimeMillis();

		//所有的告警规则集
		List<WarningRules> warningRules = warningService.warningRules();

		// 环境监控类告警暂未实现，需要cmdb采集数据
		// List<WarningRules> envRules = warningRules.stream().filter(f -> f.getTargetType() - 14 == 0).collect(Collectors.toList());

		//版本数据类告警规则集
		List<WarningRules> dataRules = warningRules.stream().filter(f -> f.getTargetType() - 14 != 0).collect(Collectors.toList());
		if (dataRules.isEmpty()) {
			return;
		}

		// 产品下所有未关闭的版本
		List<Release> releases = warningService.productOpenRelease(productId);
		for (Release release : releases) {
			try {
				releaseWarningCalculation(release, dataRules, batchNo);
			} catch (Exception e) {
				log.warn("版本【{}】告警计算发生异常", release.getRelCode(), e);
				continue;
			}
		}
	}

	/**
	 * 实时计算接口调用服务——按版本计算
	 *
	 * @param release
	 */
	public void releaseWarningCalculation(Release release) throws Exception {
		//所有的告警规则集
		List<WarningRules> warningRules = warningService.warningRules();

		// 环境监控类告警暂未实现，需要cmdb采集数据
		// List<WarningRules> envRules = warningRules.stream().filter(f -> f.getTargetType() - 14 == 0).collect(Collectors.toList());

		//版本数据类告警规则集
		List<WarningRules> dataRules = warningRules.stream().filter(f -> f.getTargetType() - 14 != 0).collect(Collectors.toList());
		if (dataRules.isEmpty()) {
			return;
		}

		releaseWarningCalculation(release, dataRules, System.currentTimeMillis());
	}

	/**
	 * 按版本计算告警方法
	 *
	 * @param release
	 * @param dataRules
	 */
	private void releaseWarningCalculation(Release release, List<WarningRules> dataRules, long batchNo) throws Exception {
		if (null == release || dataRules.isEmpty()) {
			return;
		}

		SimpleDateFormat fmt = new SimpleDateFormat(DATE_FMT);
		SimpleDateFormat fmts = new SimpleDateFormat(DATE_FMT_S);
		String warnDate = fmts.format(batchNo);

		// 计算批次号，为了兼容单独对版本、产品的计算，每次都需要创建新的batchNo，除非batchNo已存在
		WarningBatch warningBatch = new WarningBatch();
		warningBatch.setBatchNo(batchNo);
		warningBatch.setWarningDate(warnDate);
		warningBatch.setCategory(release.getRelCode());
		int batchId = warningService.createBatchAndGetId(warningBatch);

		// 汇总版本数据
		ReleaseSepData releaseSepData = warningService.groupReleaseData(release.getId(), warnDate);

		// 版本每一天的预期应发现缺陷趋势并当前计算日应发现缺陷数
		List<Map<String, Object>> defects = trendCalculation.releaseDefectTrend(release);
		Map<String, Object> expect = defects.stream().filter(f -> f.get("summaryDate").equals(warnDate)).findFirst().orElse(new HashMap<>());

		// 补充当前预期应发现缺陷数
		long expectDefectNum = expect.isEmpty() ? 0 : (long) expect.get("expectFound");
		releaseSepData.setExpectDefect(expectDefectNum);

		// 以 Aviator 支持的日期格式配置版本的关键日期点和告警计算日期
		releaseSepData.setSitBeginDate(fmt.format(fmt.parse(release.getSitBeginDate() + " 00:00:00:00")));
		releaseSepData.setUatBeginDate(fmt.format(fmt.parse(release.getUatBeginDate() + " 00:00:00:00")));
		releaseSepData.setRelDate(fmt.format(fmt.parse(release.getRelDate() + " 00:00:00:00")));
		releaseSepData.setWarnDate(fmt.format(fmt.parse(warnDate + " 00:00:00:00")));

		// SIT 的测试计划是否建立的计算数据依据
		Map<String, Object> sitPlanQuery = new HashMap<>();
		sitPlanQuery.put(CommonParameter.REL_ID, release.getId());
		sitPlanQuery.put("planType", 4);
		sitPlanQuery.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<TestPlan> sitTestPlans = testPlanDAO.testPlanQuery(sitPlanQuery);
		if (null == sitTestPlans || sitTestPlans.isEmpty()) {
			releaseSepData.setSitPlanId(null);
		} else {
			releaseSepData.setSitPlanId(Long.valueOf(sitTestPlans.get(0).getId()));
		}

		// UAT 的测试计划是否建立的计算数据依据
		Map<String, Object> uatPlanQuery = new HashMap<>();
		uatPlanQuery.put(CommonParameter.REL_ID, release.getId());
		uatPlanQuery.put("planType", 5);
		uatPlanQuery.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<TestPlan> uatTestPlans = testPlanDAO.testPlanQuery(uatPlanQuery);
		if (null == uatTestPlans || uatTestPlans.isEmpty()) {
			releaseSepData.setUatPlanId(null);
		} else {
			releaseSepData.setUatPlanId(Long.valueOf(uatTestPlans.get(0).getId()));
		}

		// SIT执行进展分析数据
		Map<String, Double> sitExecution = releaseTestExecution(release, warnDate, 4);
		releaseSepData.setSitPlaned(sitExecution.get("planed"));
		releaseSepData.setSitExecuted(sitExecution.get("executed"));

		// UAT执行进展分析数据
		Map<String, Double> uatExecution = releaseTestExecution(release, warnDate, 5);
		releaseSepData.setUatPlaned(uatExecution.get("planed"));
		releaseSepData.setUatExecuted(uatExecution.get("executed"));

		List<Warning> warnings = new ArrayList<>();

		dataRules.forEach(rule -> {
			try {
				if (!warningRuleHelper.execute(rule.getExpression(), releaseSepData)) {
					return;
				}
				Warning warning = new Warning();
				warning.setBatchId(batchId);
				warning.setProductId(release.getProductId());
				warning.setType(rule.getType());
				warning.setSubType(rule.getSubType());
				warning.setLevel(rule.getLevel());
				warning.setSummary(rule.getTitle());
				warning.setContent(rule.getExpression());
				warning.setCategory(release.getRelCode());
				warning.setResponser(getWarningReponser(release.getProductId(), rule.getTargetType()));
				warning.setWarningDate(warnDate);
				warnings.add(warning);
			} catch (Exception e) {
				log.warn("版本【{}】告警规则【{}】计算发生错误", release.getRelCode(), rule.getTitle(), e);
				return;
			}
		});

		if (warnings.size() > 0) {
			// 插入新的告警计算结果
			warningService.createWarningBatch(warnings);
		}
	}

	/**
	 * 根据产品配置的产品、开发、测试负责人和规则定义的目标负责匹配出告警归属负责人
	 *
	 * @param productId
	 * @param targetType
	 * @return
	 */
	private Integer getWarningReponser(int productId, int targetType) {
		ProductConfig productConfig = productDAO.productConfigQuery(productId);
		if (null == productConfig || StringUtils.isEmpty(productConfig.getMemberConfig())) {
			return null;
		}
		Map<String, String> memberConfig = new Gson().fromJson(productConfig.getMemberConfig(), new TypeToken<Map<String, String>>() {
		}.getType());
		if (memberConfig.isEmpty()) {
			return null;
		}

		if (targetType == ObjectType.CMS.getKey()) {
			// 开发任务相关
			return Integer.valueOf(memberConfig.get("devResponser"));
		} else if (targetType == ObjectType.TMS.getKey() || targetType == ObjectType.CASE.getKey() ||
				targetType == ObjectType.CASESTEP.getKey() || targetType == ObjectType.SCENARIO.getKey() ||
				targetType == ObjectType.TESTPLAN.getKey() || targetType == ObjectType.TESTRPT.getKey() ||
				targetType == ObjectType.DEFECT.getKey()) {
			// 测试工作相关
			return Integer.valueOf(memberConfig.get("testResponser"));
		} else if (targetType == ObjectType.REQUEST.getKey()) {
			// 产品验收相关
			return Integer.valueOf(memberConfig.get("pdResponser"));
		} else if (targetType == ObjectType.RELEASE.getKey()) {
			// 版本进展相关
			Map<String, Object> userMap = new HashMap<>();
			userMap.put(CommonParameter.PRODUCT_ID, productId);
			userMap.put("roleId", 10);
			List<User> pms = userDAO.userQueryProductRole(userMap);
			return (null == pms || pms.isEmpty()) ? null : pms.get(0).getUserId();
		}
		return null;
	}

	private Map<String, Double> releaseTestExecution(Release release, String warnDate, int planType) throws Exception {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(DATE_FMT_S);

		// 默认设置为0
		Map<String, Double> result = new HashMap<>();
		result.put("planed", 0.0);
		result.put("executed", 0.0);
		double index = 1.0;

		// 查询该版本指定计划类型的测试集用例
		Map<String, Object> sitQueryMap = new HashMap<>();
		sitQueryMap.put("reportDate", warnDate);
		sitQueryMap.put("planType", planType);
		sitQueryMap.put(CommonParameter.REL_ID, release.getId());
		String sitCaseStr = testReportDAO.relCasesStrQuery(sitQueryMap);

		if (StringUtils.isEmpty(sitCaseStr)) {
			return result;
		}

		// 该版本指定计划类型的已执行测试用例总数
		Integer runedSitCasesCount = testReportDAO.runedCaseQuery(sitQueryMap);

		double sitRatio;
		Date beginDate;
		if (planType == 4) {
			beginDate = format.parse(release.getSitBeginDate());
		} else if (planType == 5) {
			beginDate = format.parse(release.getUatBeginDate());
		} else {
			log.warn("暂不支持的测试计划类型数据！");
			return result;
		}

		Date endDate = format.parse(release.getRelDate());
		Date nowDate = format.parse(warnDate);
		cal.setTime(nowDate);

		// 当前应执行比率计算
		if (cal.getTime().before(beginDate)) {    // 进入测试周期之前默认为0
			sitRatio = 0.0;
		} else if (cal.getTime().after(endDate)) {    // 测试周期结束之后默认为1
			sitRatio = 1.0;
		} else {    // 测试计划结束时间减去当前时间 除以 测试计划结束时间减去测试计划开始时间
			sitRatio = (endDate.getTime() - nowDate.getTime()) / (endDate.getTime() - beginDate.getTime());
		}

		result.put("planed", index * sitCaseStr.split(",").length * sitRatio);
		result.put("executed", index * runedSitCasesCount);

		return result;
	}
}
