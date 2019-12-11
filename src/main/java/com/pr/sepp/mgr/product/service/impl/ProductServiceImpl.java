package com.pr.sepp.mgr.product.service.impl;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.mgr.product.model.ProductBranch;
import com.pr.sepp.mgr.product.model.ProductConfig;
import com.pr.sepp.mgr.product.model.ProductDoc;
import com.pr.sepp.mgr.product.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

@Transactional
@Service("productService")
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private HistoryService historyService;

	@Override
	public List<Product> productQuery(Map<String, Object> dataMap) {
		return productDAO.productQuery(dataMap);
	}

	@Override
	public int productCreate(Product product) {
		int userId = ParameterThreadLocal.getUserId();

		productDAO.productCreate(product);
		int created = product.getProductId();

		SEPPHistory history = new SEPPHistory();
		history.setObjType(11);
		history.setObjId(created);
		history.setObjKey("product_id");
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(userId);
		history.setOperComment("新建产品，编码为【" + product.getProductCode() + "】，名称为【" + product.getProductName() + "】");
		history.setOperType(1);
		history.setReferUser(product.getOwner());
		historyService.historyInsert(history);

		return created;
	}

	@Override
	public int productUpdate(Product product) throws IllegalAccessException {
		int productId = product.getProductId();
		int userId = product.getUserId();

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("productId", productId);
		Product oldProduct = productDAO.productQuery(queryMap).get(0);

		product.setProductId(oldProduct.getProductId());

		if (!oldProduct.getProductCode().equals(product.getProductCode())) {
			Map<String, Object> relModMap = new HashMap<>();
			relModMap.put("productId", productId);
			relModMap.put("newProductCode", product.getProductCode());
			relModMap.put("oldProductCode", oldProduct.getProductCode());

			productDAO.updateReleaseAlias(relModMap);
		}

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends Product> cls = product.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(product);
			Object oldValue = field.get(oldProduct);

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(11);
				history.setObjId(product.getProductId());
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment("更新产品【" + product.getProductCode() + "】，名称为【" + product.getProductName() + "】");
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
		return productDAO.productUpdate(product);
	}

	@Override
	public int productDelete(int productId) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("productId", productId);
		Product oldProduct = productDAO.productQuery(queryMap).get(0);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(11);
		history.setObjId(productId);
		history.setObjKey("is_valid");
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(3);
		history.setOperComment("删除编码为【" + oldProduct.getProductCode() + "】、名称为【" + oldProduct.getProductName() + "】的产品");
		history.setReferUser(oldProduct.getOwner());
		history.setOrgValue("Y");
		history.setNewValue("N");
		historyService.historyInsert(history);

		return productDAO.productDelete(productId);
	}

	@Override
	public Map<String, Object> productConfigQuery(Integer productId) {
		List<ProductConfig> productConfigs = productDAO.productConfigQuery(productId);
		if (null == productConfigs || productConfigs.size() == 0) {
			return null;
		}
		ProductConfig productConfig = productConfigs.get(0);
		Map<String, Object> config = new HashMap<>();
		config.put("changeAuditor", productConfig.getChangeAuditor());
		config.put("dreTarget", productConfig.getDreTarget());

		if (StringUtils.isNotEmpty(productConfig.getMemberConfig())) {
			config.put("memberConfig", new Gson().fromJson(productConfig.getMemberConfig(), new TypeToken<Map<String, Object>>() {
			}.getType()));
		}

		if (StringUtils.isNotEmpty(productConfig.getGompertzDefine())) {
			config.put("gompertzDefine", new Gson().fromJson(productConfig.getGompertzDefine(), new TypeToken<Map<String, Object>>() {
			}.getType()));
		}

		if (StringUtils.isNotEmpty(productConfig.getGompertzParams())) {
			config.put("gompertzParams", new Gson().fromJson(productConfig.getGompertzParams(), new TypeToken<Map<String, Object>>() {
			}.getType()));
		}

		return config;
	}

	@Override
	public int productConfigUpdate(ProductConfig productConfig) {
		return productDAO.productConfigUpdate(productConfig);
	}

	@Override
	public List<ProductDoc> productDocQuery(Map<String, Object> dataMap) {
		return productDAO.productDocQuery(dataMap);
	}

	@Override
	public List<ProductDoc> documentFuzzQuery(Map<String, Object> dataMap) {
		return productDAO.documentFuzzQuery(dataMap);
	}

	@Override
	public List<String> documentVersionQuery(int productId) {
		return productDAO.documentVersionQuery(productId);
	}

	@Override
	public int productDocCreate(ProductDoc doc) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("productId", doc.getProductId());
		dataMap.put("productId", doc.getParentId());
		dataMap.put("attachmentId", doc.getAttachmentId());

		// 查询是否已存在，如果已存在则直接返回
		List<ProductDoc> productDocs = productDAO.productDocQuery(dataMap);
		if (null != productDocs && productDocs.size() > 0) {
			return 0;
		}

		productDAO.productDocCreate(doc);
		int id = doc.getId();

		SEPPHistory history = new SEPPHistory();
		String type = doc.getType().equals("folder") ? "目录" : "";
		int userId = ParameterThreadLocal.getUserId();
		history.setObjType(19);
		history.setObjId(id);
		history.setObjKey("id");
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(userId);
		history.setOperComment("新建产品文档" + type + "，编码为【" + id + "】，名称为【" + doc.getLabel() + "】");
		history.setOperType(1);
		history.setReferUser(userId);
		historyService.historyInsert(history);

		return id;
	}

	@Override
	public int productDocUpdate(ProductDoc doc) throws IllegalAccessException {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int id = doc.getId();

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("id", id);
		ProductDoc oldDoc = productDAO.productDocQuery(queryMap).get(0);

		// readonly
		doc.setProductId(oldDoc.getProductId());
		doc.setModuleId(oldDoc.getModuleId());
		doc.setType(oldDoc.getType());
		doc.setReqId(oldDoc.getReqId());
		doc.setUploadDate(oldDoc.getUploadDate());
		doc.setUploadUser(oldDoc.getUploadUser());
		doc.setAttachmentId(oldDoc.getAttachmentId());
		doc.setFileName(oldDoc.getFileName());

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends ProductDoc> cls = doc.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(doc);
			Object oldValue = field.get(oldDoc);

			if (keyName.endsWith("Name") || keyName.equals("url") || keyName.equals("reqSummary")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(19);
				history.setObjId(id);
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment("更新产品文档【" + id + "】，名称为【" + doc.getLabel() + "】");
				history.setReferUser(doc.getUploadUser());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}

		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return productDAO.productDocUpdate(doc);
	}

	@Override
	public int productDocDelete(int attachmentId) {
		return productDAO.productDocDelete(attachmentId);
	}

	@Override
	public List<ProductBranch> productBranchQuery(Map<String, Object> dataMap) {
		return productDAO.productBranchQuery(dataMap);
	}

	@Override
	public List<Map<String, Object>> documentReqQuery(int attachmentId) {
		return productDAO.documentReqQuery(ParameterThreadLocal.getProductId(), attachmentId);
	}

	@Override
	public int productBranchCreate(ProductBranch branch) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		branch.setProductId(productId);
		branch.setCreator(userId);
		productDAO.productBranchCreate(branch);
		int branchId = branch.getBranchId();

		SEPPHistory history = new SEPPHistory();
		history.setObjType(20);
		history.setObjId(branchId);
		history.setObjKey("branch_id");
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperComment("新建产品分支，编码为【" + branch.getBranchId() + "】，名称为【" + branch.getBranchName() + "】");
		history.setOperType(1);
		history.setReferUser(userId);
		historyService.historyInsert(history);

		return branchId;
	}

	@Override
	public int productBranchUpdate(ProductBranch branch) throws IllegalAccessException {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int branchId = branch.getBranchId();

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("branchId", branchId);
		ProductBranch oldBranch = productDAO.productBranchQuery(queryMap).get(0);

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends ProductBranch> cls = branch.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(branch);
			Object oldValue = field.get(oldBranch);

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(20);
				history.setObjId(branchId);
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment("产品分支【" + oldBranch.getBranchName() + "】信息更新");
				history.setReferUser(oldBranch.getCreator());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return productDAO.productBranchUpdate(branch);
	}

	@Override
	public int productBranchDelete(Integer branchId) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("branchId", branchId);
		ProductBranch oldBranch = productDAO.productBranchQuery(queryMap).get(0);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(20);
		history.setObjId(branchId);
		history.setObjKey("is_valid");
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(3);
		history.setOperComment("禁用产品分支【" + oldBranch.getBranchName() + "】");
		history.setReferUser(oldBranch.getCreator());
		history.setOrgValue("1");
		history.setNewValue("0");
		historyService.historyInsert(history);

		return productDAO.productBranchDelete(branchId);
	}

}
