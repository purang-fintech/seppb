package com.pr.sepp.base.controller;

import java.util.Map;

import com.pr.sepp.common.constants.CommonParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.pr.sepp.base.service.BaseQueryService;

@RestController
@ResponseBody
public class BaseQueryController {
    @Autowired
    private BaseQueryService baseQueryService;

    @RequestMapping(value = "/base/query_p", method = RequestMethod.POST)
    public Map<String, Object> baseQueryProduct(@RequestParam(value = CommonParameter.PRODUCT_ID, required = true) int productId) {
        return baseQueryService.baseQueryProduct(productId);
    }

    @RequestMapping(value = "/base/query", method = RequestMethod.POST)
    public Map<String, Object> baseQueryNonParams() {
        return baseQueryService.baseQueryNonParams();
    }
}
