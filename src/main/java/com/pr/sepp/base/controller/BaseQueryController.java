package com.pr.sepp.base.controller;

import com.pr.sepp.base.model.Product;
import com.pr.sepp.base.service.BaseQueryService;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.mgr.product.model.ProductBranch;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class BaseQueryController {
	@Autowired
	private BaseQueryService baseQueryService;

	@RequestMapping(value = "/base/query_p", method = RequestMethod.POST)
	public Map<String, Object> baseQueryProduct(@RequestParam(value = CommonParameter.PRODUCT_ID, required = true) Integer productId) {
		return baseQueryService.baseQueryProduct(productId);
	}

	@RequestMapping(value = "/base/query", method = RequestMethod.POST)
	public Map<String, Object> baseQueryNonParams() {
		return baseQueryService.baseQueryNonParams();
	}

	@RequestMapping(value = "/base/products", method = RequestMethod.POST)
	public List<Product> baseQueryProducts() {
		return baseQueryService.product();
	}

	@RequestMapping(value = "/base/branch/{productId}", method = RequestMethod.POST)
	public List<ProductBranch> baseQueryBranches(@PathVariable("productId") Integer productId) {
		return baseQueryService.productBranch(productId);
	}
}
