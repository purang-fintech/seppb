package com.pr.sepp.mgr.product.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.mgr.product.model.ProductBranch;
import com.pr.sepp.mgr.product.model.ProductConfig;
import com.pr.sepp.mgr.product.model.ProductDoc;
import com.pr.sepp.mgr.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class ProductController {
	@Autowired
	private ProductService productService;

	private static final String PD_NAME = "productName";
	private static final String PD_CODE = "productCode";

	@PostMapping(value = "/product/query")
	public PageInfo<Product> productQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(PD_CODE, request.getParameter(PD_CODE));
		dataMap.put(PD_NAME, request.getParameter(PD_NAME));
		dataMap.put("owner", request.getParameter("owner"));
		dataMap.put("isValid", request.getParameter("isValid"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<Product> list = productService.productQuery(dataMap);
		PageInfo<Product> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@PostMapping(value = "/product/exists")
	public int productExists(HttpServletRequest request) {
		return productService.productExists(request.getParameter(PD_NAME), request.getParameter(PD_CODE));
	}

	@PostMapping(value = "/product/create")
	public int productCreate(@RequestBody Product product) {
		return productService.productCreate(product);
	}

	@PostMapping(value = "/product/update")
	public int productUpdate(@RequestBody Product product) throws IllegalAccessException {
		return productService.productUpdate(product);
	}

	@PostMapping(value = "/product/delete/{productId}")
	public int productDelete(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
		return productService.productDelete(productId);
	}

	@PostMapping(value = "/product/config_query/{productId}")
	public Map<String, Object> productConfigQuery(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
		return productService.productConfigQuery(productId);
	}

	@PostMapping(value = "/product/config_update")
	public int productConfigUpdate(@RequestBody ProductConfig productConfig) {
		return productService.productConfigUpdate(productConfig);
	}

	@PostMapping(value = "/document/query")
	public List<ProductDoc> productDocQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.MODULE_ID, request.getParameter(CommonParameter.MODULE_ID));
		dataMap.put("parentId", request.getParameter("parentId"));
		dataMap.put("type", request.getParameter("type"));

		return productService.productDocQuery(dataMap);
	}

	@PostMapping(value = "/document/fuzz_query")
	public PageInfo<ProductDoc> documentFuzzQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("searchText", request.getParameter("searchText"));
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<ProductDoc> list = productService.documentFuzzQuery(dataMap);
		PageInfo<ProductDoc> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@PostMapping(value = "/document/version_query/{productId}")
	public List<String> documentVersionQuery(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
		return productService.documentVersionQuery(productId);
	}

	@PostMapping(value = "/document/req_query/{attachmentId}")
	public List<Map<String, Object>> documentReqQuery(@PathVariable("attachmentId") Integer attachmentId) {
		return productService.documentReqQuery(attachmentId);
	}

	@PostMapping(value = "/document/create")
	public int productDocCreate(@RequestBody ProductDoc doc) {
		return productService.productDocCreate(doc);
	}

	@PostMapping(value = "/document/update")
	public int productDocUpdate(@RequestBody ProductDoc doc) throws IllegalAccessException {
		return productService.productDocUpdate(doc);
	}

	@PostMapping(value = "/branch/query")
	public PageInfo<ProductBranch> productBranchQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put("isValid", request.getParameter("isValid"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<ProductBranch> list = productService.productBranchQuery(dataMap);
		PageInfo<ProductBranch> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@PostMapping(value = "/branch/create")
	public int productBranchCreate(@RequestBody ProductBranch branch) {
		return productService.productBranchCreate(branch);
	}

	@PostMapping(value = "/branch/update")
	public int productBranchUpdate(@RequestBody ProductBranch branch) throws IllegalAccessException {
		return productService.productBranchUpdate(branch);
	}

	@PostMapping(value = "/branch/delete/{branchId}")
	public int productBranchDelete(@PathVariable(CommonParameter.BRANCH_ID) Integer branchId) {
		return productService.productBranchDelete(branchId);
	}
}