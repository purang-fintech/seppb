package com.pr.sepp.auth.controller;

import com.github.pagehelper.PageInfo;
import com.pr.sepp.auth.model.Role;
import com.pr.sepp.auth.model.resp.CurrentUserRoleResp;
import com.pr.sepp.auth.service.UserRoleService;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping(value = "/user/roles")
    public PageInfo<Role> listPagingRoles(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        return userRoleService.allRoles(null, pageNum, pageSize);
    }

    @PostMapping(value = "/user/roles")
    public void add(@RequestBody Role role) {
        userRoleService.save(role);
    }

    @DeleteMapping(value = "/user/roles/{roleId}")
    public void deleteRole(@PathVariable("roleId") Integer roleId) {
        userRoleService.deleteRole(roleId);
    }

    @GetMapping(value = "/user/roles/current-user")
    public CurrentUserRoleResp rolesByUser() {
        return userRoleService.rolesByUser(ParameterThreadLocal.getUserId(), ParameterThreadLocal.getProductId());
    }

    @GetMapping(value = "/user/product/roles")
    public CurrentUserRoleResp userProductRoles() {
        return userRoleService.currentUserRoles(ParameterThreadLocal.getUserId());
    }

    @GetMapping(value = "/user/product/roles/{productId}")
    public CurrentUserRoleResp userRolesByProduct(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
        return userRoleService.currentUserRolesByProductId(ParameterThreadLocal.getUserId(), productId);
    }

    @GetMapping(value = "/user/all-roles")
    public List<Role> listRoles() {
        return userRoleService.allRoles(null);
    }

}
