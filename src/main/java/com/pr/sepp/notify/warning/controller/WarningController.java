package com.pr.sepp.notify.warning.controller;

import com.pr.sepp.common.calculation.service.WarningCalculation;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.sep.release.dao.ReleaseDAO;
import com.pr.sepp.sep.release.model.Release;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping(value = "/warning/release_cal/{relId}")
	public String releaseWarningCalculation(@PathVariable("relId") int relId) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, relId);
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<Release> releases = releaseDAO.releaseQuery(dataMap);
		if (null == releases || releases.isEmpty()) {
			return "版本不存在，请确认查询条件！";
		}

		Release release = releases.get(0);

		try {
			warningCalculation.releaseWarningCalculation(release);
			return "版本【" + release.getRelCode() + "】提交重新计算成功！";
		} catch (Exception e) {
			log.error("版本【{}】告警计算发生异常", release.getRelCode(), e);
			return "版本【" + release.getRelCode() + "】告警计算发生异常！";
		}
	}

	@PostMapping(value = "/warning/release_cal/{productId}")
	public String productWarningCalculation(@PathVariable("productId") Integer productId) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, productId);
		List<Product> products = productDAO.productQuery(dataMap);
		if (null == products || products.isEmpty()) {
			return "产品不存在，请确认查询条件！";
		}

		Product product = products.get(0);

		try {
			warningCalculation.productWarningCalculation(productId);
			return "产品【" + product.getProductCode() + "】提交重新计算成功！";
		} catch (Exception e) {
			log.error("产品【{}】告警计算发生异常", product.getProductCode(), e);
			return "产品【" + product.getProductCode() + "】告警计算发生异常！";
		}
	}
}
