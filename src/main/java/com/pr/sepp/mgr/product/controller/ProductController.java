package com.pr.sepp.mgr.product.controller;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.mgr.product.model.ProductBranch;
import com.pr.sepp.mgr.product.model.ProductConfig;
import com.pr.sepp.mgr.product.model.ProductDoc;
import com.pr.sepp.mgr.product.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

	@RequestMapping(value = "/product/query", method = RequestMethod.POST)
	public PageInfo<Product> productQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put("productCode", request.getParameter("productCode"));
		dataMap.put("productName", request.getParameter("productName"));
		dataMap.put("owner", request.getParameter("owner"));
		dataMap.put("isValid", request.getParameter("isValid"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<Product> list = productService.productQuery(dataMap);
		PageInfo<Product> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/product/create", method = RequestMethod.POST)
	public int productCreate(@RequestBody Product product) {
		return productService.productCreate(product);
	}

	@RequestMapping(value = "/product/update", method = RequestMethod.POST)
	public int productUpdate(@RequestBody Product product) throws IllegalAccessException {
		return productService.productUpdate(product);
	}

	@RequestMapping(value = "/product/delete/{productId}", method = RequestMethod.POST)
	public int productDelete(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
		return productService.productDelete(productId);
	}

	@RequestMapping(value = "/product/config_query/{productId}", method = RequestMethod.POST)
	public Map<String, Object> productConfigQuery(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
		return productService.productConfigQuery(productId);
	}

	@RequestMapping(value = "/product/config_update", method = RequestMethod.POST)
	public int productConfigUpdate(@RequestBody ProductConfig productConfig) {
		return productService.productConfigUpdate(productConfig);
	}

	@RequestMapping(value = "/document/query", method = RequestMethod.POST)
	public List<ProductDoc> productDocQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.MODULE_ID, request.getParameter(CommonParameter.MODULE_ID));
		dataMap.put("parentId", request.getParameter("parentId"));
		dataMap.put("type", request.getParameter("type"));

		return productService.productDocQuery(dataMap);
	}

	@RequestMapping(value = "/document/fuzz_query", method = RequestMethod.POST)
	public PageInfo<ProductDoc> documentFuzzQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("searchText", request.getParameter("searchText"));
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<ProductDoc> list = productService.documentFuzzQuery(dataMap);
		PageInfo<ProductDoc> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/document/version_query/{productId}", method = RequestMethod.POST)
	public List<String> documentVersionQuery(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
		return productService.documentVersionQuery(productId);
	}

	@PostMapping(value = "/document/req_query/{attachmentId}")
	public List<Map<String, Object>> documentReqQuery(@PathVariable("attachmentId") Integer attachmentId) {
		return productService.documentReqQuery(attachmentId);
	}

	@RequestMapping(value = "/document/create", method = RequestMethod.POST)
	public int productDocCreate(@RequestBody ProductDoc doc) {
		return productService.productDocCreate(doc);
	}

	@RequestMapping(value = "/document/update", method = RequestMethod.POST)
	public int productDocUpdate(@RequestBody ProductDoc doc) throws IllegalAccessException {
		return productService.productDocUpdate(doc);
	}

	@RequestMapping(value = "/branch/query", method = RequestMethod.POST)
	public PageInfo<ProductBranch> productBranchQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put("isValid", request.getParameter("isValid"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<ProductBranch> list = productService.productBranchQuery(dataMap);
		PageInfo<ProductBranch> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/branch/create", method = RequestMethod.POST)
	public int productBranchCreate(@RequestBody ProductBranch branch) {
		return productService.productBranchCreate(branch);
	}

	@RequestMapping(value = "/branch/update", method = RequestMethod.POST)
	public int productBranchUpdate(@RequestBody ProductBranch branch) throws IllegalAccessException {
		return productService.productBranchUpdate(branch);
	}

	@RequestMapping(value = "/branch/delete/{branchId}", method = RequestMethod.POST)
	public int productBranchDelete(@PathVariable(CommonParameter.BRANCH_ID) Integer branchId) {
		return productService.productBranchDelete(branchId);
	}
}