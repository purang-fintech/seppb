package com.pr.sepp.env.info.service.impl;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.env.info.dao.EnvInfoDAO;
import com.pr.sepp.env.info.model.EnvInfo;
import com.pr.sepp.env.info.service.EnvInfoService;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.product.model.Product;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service("envInfoService")
public class EnvInfoServiceImpl implements EnvInfoService {

	@Autowired
	private EnvInfoDAO envInfoDAO;

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private HistoryService historyService;

	@Override
	public List<EnvInfo> envInfoQuery(Map<String, Object> dataMap) {
		return envInfoDAO.envInfoQuery(dataMap);
	}

	@Override
	public List<String> instanceQuery(Integer productId) {
		return envInfoDAO.instanceQuery(productId);
	}

	@Override
	public int envInfoCreate(EnvInfo envInfo) {
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put(CommonParameter.PRODUCT_ID, envInfo.getProductId());
		Product product = productDAO.productQuery(tempMap).get(0);

		envInfoDAO.envInfoCreate(envInfo);

		int created = envInfo.getId();

		SEPPHistory history = new SEPPHistory();
		history.setObjType(14);
		history.setObjId(created);
		history.setObjKey(CommonParameter.ID);
		history.setProductId(product.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperComment("产品【" + product.getProductName() + "】下新建环境记录，URL为【" + envInfo.getEnvUrl() + "】");
		history.setOperType(1);
		historyService.historyInsert(history);

		return created;
	}

	@Override
	public int envInfoUpdate(EnvInfo envInfo) throws IllegalAccessException {
		Integer productId = ParameterThreadLocal.getProductId();
		Integer userId = ParameterThreadLocal.getUserId();

		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put(CommonParameter.PRODUCT_ID, productId);
		Product product = productDAO.productQuery(tempMap).get(0);

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(CommonParameter.ID, envInfo.getId());
		EnvInfo oldEnvInfo = envInfoDAO.envInfoQuery(queryMap).get(0);

		envInfo.setProductId(productId);
		SEPPHistory history = new SEPPHistory();
		history.setObjType(14);
		history.setObjId(envInfo.getId());
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(2);
		history.setOperComment("产品【" + product.getProductName() + "】下环境记录更新，URL为【" + envInfo.getEnvUrl() + "】");

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends EnvInfo> cls = envInfo.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(envInfo);
			Object oldValue = field.get(oldEnvInfo);

			if (keyName.endsWith("Name")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (CollectionUtils.isNotEmpty(histories)) {
			historyService.historyInsertBatch(histories);
		}

		return envInfoDAO.envInfoUpdate(envInfo);
	}

	@Override
	public int envInfoDelete(Integer envInfoId) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("envInfoId", envInfoId);
		EnvInfo oldEnvInfo = envInfoDAO.envInfoQuery(queryMap).get(0);

		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put(CommonParameter.PRODUCT_ID, oldEnvInfo.getProductId());
		Product product = productDAO.productQuery(tempMap).get(0);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(14);
		history.setObjId(envInfoId);
		history.setObjKey(CommonParameter.ID);
		history.setProductId(product.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(3);
		history.setOperComment("产品【" + product.getProductName() + "】下环境记录被删除，URL为【" + oldEnvInfo.getEnvUrl() + "】");
		historyService.historyInsert(history);

		return envInfoDAO.envInfoDelete(envInfoId);
	}

	@Override
	public Map<String, String> getJobNames(Integer envType, Integer branchId) {
		List<EnvInfo> envInfos = envInfoDAO.findEnvInfos(ParameterThreadLocal.getProductId(), branchId, envType);
		return envInfos.stream().collect(Collectors.toMap(EnvInfo::getInstance, EnvInfo::getJobName));
	}

}
