package com.pr.sepp.notify.warning.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.calculation.service.WarningCalculation;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.notify.warning.model.WarningMessage;
import com.pr.sepp.notify.warning.model.WarningQuery;
import com.pr.sepp.notify.warning.service.WarningService;
import com.pr.sepp.sep.release.dao.ReleaseDAO;
import com.pr.sepp.sep.release.model.Release;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class WarningController {

	@Autowired
	private WarningCalculation warningCalculation;

	@Autowired
	ReleaseDAO releaseDAO;

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private WarningService warningService;

	@PostMapping(value = "/warning/query")
	public PageInfo<WarningMessage> warningQuery(@RequestBody WarningQuery warningQuery) {
		warningQuery.setProductId(ParameterThreadLocal.getProductId());
		warningQuery.setSendGateway(2);

		PageHelper.startPage(warningQuery.getPageNum(), warningQuery.getPageSize());

		List<WarningMessage> list = warningService.warningQuery(warningQuery);
		PageInfo<WarningMessage> pageInfo = new PageInfo<>(list);

		return pageInfo;
	}

	@PostMapping(value = "/warning/release_cal/{relId}")
	public Map<String, Object> releaseWarningCalculation(@PathVariable("relId") int relId) {
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, relId);
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<Release> releases = releaseDAO.releaseQuery(dataMap);
		if (null == releases || releases.isEmpty()) {
			result.put("code", 0);
			result.put("message", "版本不存在，请确认查询条件！");
			return result;
		}

		Release release = releases.get(0);

		try {
			warningCalculation.releaseWarningCalculation(release);
			result.put("code", 1);
			result.put("message", "版本【" + release.getRelCode() + "】提交重新计算成功！");
			return result;
		} catch (Exception e) {
			log.error("版本【{}】告警计算发生异常", release.getRelCode(), e);
			result.put("code", 2);
			result.put("message", "版本【" + release.getRelCode() + "】告警计算发生异常！");
			return result;
		}
	}

	@PostMapping(value = "/warning/product_cal/{productId}")
	public Map<String, Object> productWarningCalculation(@PathVariable("productId") Integer productId) {
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, productId);
		List<Product> products = productDAO.productQuery(dataMap);
		if (null == products || products.isEmpty()) {
			result.put("code", 0);
			result.put("message", "产品不存在，请确认查询条件！");
			return result;
		}

		Product product = products.get(0);

		try {
			warningCalculation.productWarningCalculation(productId);
			result.put("code", 1);
			result.put("message", "产品【" + product.getProductCode() + "】提交重新计算成功！");
			return result;
		} catch (Exception e) {
			log.error("产品【{}】告警计算发生异常", product.getProductCode(), e);
			result.put("code", 1);
			result.put("message", "产品【" + product.getProductCode() + "】告警计算发生异常！");
			return result;
		}
	}
}
