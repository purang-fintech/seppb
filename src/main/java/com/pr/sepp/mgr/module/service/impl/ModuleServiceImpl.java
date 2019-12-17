package com.pr.sepp.mgr.module.service.impl;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.module.dao.ModuleDAO;
import com.pr.sepp.mgr.module.model.Module;
import com.pr.sepp.mgr.module.service.ModuleService;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Transactional
@Service("moduleService")
public class ModuleServiceImpl implements ModuleService {

	@Autowired
	private ModuleDAO moduleDAO;

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private HistoryService historyService;

	@Override
	public List<Module> moduleQuery(Map<String, Object> dataMap) {
		return moduleDAO.moduleQuery(dataMap);
	}

	@Override
	public int moduleCreate(Module module) {
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put(CommonParameter.PRODUCT_ID, module.getProductId());
		Product product = productDAO.productQuery(tempMap).get(0);

		moduleDAO.moduleCreate(module);
		int moduleId = module.getModuleId();

		SEPPHistory history = new SEPPHistory();
		history.setObjType(12);
		history.setObjId(moduleId);
		history.setProductId(module.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(1);
		history.setOperComment("产品【 " + product.getProductCode() + "】下新建模块【" + module.getModuleName() + "】");
		history.setReferUser(product.getOwner());
		historyService.historyInsert(history);

		return moduleId;
	}

	@Override
	public int moduleUpdate(Module module) throws IllegalAccessException {
		Integer productId = ParameterThreadLocal.getProductId();
		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put(CommonParameter.PRODUCT_ID, productId);
		Product product = productDAO.productQuery(tempMap).get(0);

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.MODULE_ID, module.getModuleId());
		Module oldModule = moduleDAO.moduleQuery(dataMap).get(0);

		int userId = ParameterThreadLocal.getUserId();

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends Module> cls = module.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(module);
			Object oldValue = field.get(oldModule);

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(12);
				history.setObjId(module.getModuleId());
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment("更新产品【" + product.getProductName() + "】下【" + module.getModuleName() + "】模块数据");
				history.setReferUser(product.getOwner());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return moduleDAO.moduleUpdate(module);
	}

	@Override
	public int moduleDelete(Integer moduleId) {
		Map<String, Object> modMap = new HashMap<>();
		modMap.put(CommonParameter.MODULE_ID, moduleId);
		Module module = moduleDAO.moduleQuery(modMap).get(0);

		Map<String, Object> tempMap = new HashMap<>();
		tempMap.put(CommonParameter.PRODUCT_ID, module.getProductId());
		Product product = productDAO.productQuery(tempMap).get(0);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(12);
		history.setObjId(moduleId);
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setProductId(module.getProductId());
		history.setOperType(3);
		history.setOperComment("删除/禁用产品【" + product.getProductCode() + "】下【" + module.getModuleName() + "】模块数据");
		history.setReferUser(product.getOwner());
		history.setObjKey("is_valid");
		history.setOrgValue("Y");
		history.setNewValue("N");
		historyService.historyInsert(history);

		return moduleDAO.moduleDelete(moduleId);
	}

}
