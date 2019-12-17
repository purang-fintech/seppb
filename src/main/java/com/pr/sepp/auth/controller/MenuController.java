package com.pr.sepp.auth.controller;

import com.pr.sepp.auth.model.Menu;
import com.pr.sepp.auth.model.req.MenuReq;
import com.pr.sepp.auth.model.resp.MenuResp;
import com.pr.sepp.auth.model.resp.UserAuthResp;
import com.pr.sepp.auth.service.MenuService;
import com.pr.sepp.common.constants.CommonParameter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static com.pr.sepp.auth.model.Menu.listToString;

@RestController
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping(value = "/menus/configs")
    public List<MenuResp> listMenus(@RequestParam(value = "initValue", defaultValue = "1") Integer value) {
        return menuService.menuRespList(value);
    }

    @PostMapping(value = "/menus/configs")
    public void addMenu(@Valid @RequestBody MenuReq menuReq) {
        Menu menu = Menu.builder().build();
        BeanUtils.copyProperties(menuReq, menu);
        menu.setRoles(listToString(menu.getRoleIds()));
        menuService.addMenu(menu);
    }

    @PutMapping(value = "/menus/configs")
    public void updateMenu(@RequestBody MenuReq menuReq) {
        Menu menu = Menu.builder().build();
        BeanUtils.copyProperties(menuReq, menu);
        menuService.updateMenu(menu);
    }

    @DeleteMapping(value = "/menus/configs/{id}")
    public void deleteMenu(@PathVariable(CommonParameter.ID) Integer id) {
        menuService.deleteMenu(id);
    }

    @GetMapping(value = "/router/auth")
    public UserAuthResp checkRouterAuth(HttpServletRequest request, HttpServletResponse response, @RequestParam("router") String router) {
        return menuService.hasAuth(router, request, response);
    }
}
