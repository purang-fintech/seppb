package com.pr.sepp.mgr.product.service;

import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.mgr.product.model.ProductBranch;
import com.pr.sepp.mgr.product.model.ProductConfig;
import com.pr.sepp.mgr.product.model.ProductDoc;

import java.util.List;
import java.util.Map;

public interface ProductService {

	List<Product> productQuery(Map<String, Object> dataMap);

	int productExists(String productName, String productCode);

	int productCreate(Product product);

	int productUpdate(Product product) throws IllegalAccessException;

	int productDelete(int productId);

	Map<String, Object> productConfigQuery(Integer productId);

	int productConfigUpdate(ProductConfig productConfig);

	List<ProductDoc> productDocQuery(Map<String, Object> dataMap);

	List<ProductDoc> documentFuzzQuery(Map<String, Object> dataMap);

	List<String> documentVersionQuery(int productId);

	int productDocCreate(ProductDoc product);

	int productDocUpdate(ProductDoc product) throws IllegalAccessException;

	int productDocDelete(int attachmentId);

	List<ProductBranch> productBranchQuery(Map<String, Object> dataMap);

	List<Map<String, Object>> documentReqQuery(int attachmentId);

	int productBranchCreate(ProductBranch branch);

	int productBranchUpdate(ProductBranch branch) throws IllegalAccessException;

	int productBranchDelete(Integer branchId);

}
