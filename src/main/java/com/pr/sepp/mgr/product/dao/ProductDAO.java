package com.pr.sepp.mgr.product.dao;

import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.mgr.product.model.ProductBranch;
import com.pr.sepp.mgr.product.model.ProductConfig;
import com.pr.sepp.mgr.product.model.ProductDoc;

import java.util.List;
import java.util.Map;

public interface ProductDAO {

	List<Product> productQuery(Map<String, Object> dataMap);

	int productCreate(Product product);

	int productUpdate(Product product);

	int productDelete(int productId);

	int updateReleaseAlias(Map<String, Object> product);

	ProductConfig productConfigQuery(int productId);

	int productConfigCreate(ProductConfig productConfig);

	int productConfigUpdate(ProductConfig productConfig);

	List<ProductDoc> productDocQuery(Map<String, Object> dataMap);

	List<ProductDoc> documentFuzzQuery(Map<String, Object> dataMap);

	List<String> documentVersionQuery(Integer productId);

	int productDocCreate(ProductDoc product);

	int productDocUpdate(ProductDoc product);

	int productDocDelete(Integer attachmentId);

	List<ProductBranch> productBranchQuery(Map<String, Object> dataMap);

	List<Map<String, Object>> documentReqQuery(Integer productId, int attachmentId);

	int productBranchCreate(ProductBranch branch);

	int productBranchUpdate(ProductBranch branch);

	int productBranchDelete(Integer branchId);

	ProductBranch findProductBranch(Integer branchId);

	int productExists(String productName, String productCode);
}
