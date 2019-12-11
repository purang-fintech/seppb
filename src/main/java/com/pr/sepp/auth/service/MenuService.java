package com.pr.sepp.auth.service;

import com.pr.sepp.auth.dao.MenuDAO;
import com.pr.sepp.auth.dao.UserRoleDAO;
import com.pr.sepp.auth.model.Menu;
import com.pr.sepp.auth.model.Privilege;
import com.pr.sepp.auth.model.resp.MenuResp;
import com.pr.sepp.auth.model.resp.UserAuthResp;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pr.sepp.auth.model.Menu.stringToList;
import static java.util.Objects.isNull;

@Service
@Slf4j
public class MenuService {
    @Autowired
    private MenuDAO menuDAO;

    @Autowired
    private UserRoleDAO userRoleDAO;
    @Autowired
    private UserRoleService userRoleService;


    public List<MenuResp> menuRespList(final int value) {
        List<Menu> menus = menuDAO.findAllMenus();
        List<MenuResp> menuResps = menus.stream().filter(menu -> menu.getParentId() == value).map(menu -> MenuResp.builder()
                .title(menu.getTitle())
                .label(menu.getTitle())
                .index(menu.getMenuIndex())
                .icon(menu.getMenuIcon())
                .id(menu.getId())
                .shows(stringToList(menu.getRoles()))
                .roleIds(stringToList(menu.getRoles()))
                .build()).collect(Collectors.toList());
        for (MenuResp menuResp : menuResps) {
            List<MenuResp> subs = findSubs(menuResp.getId(), menus);
            menuResp.setSubs(subs);
            menuResp.setChildren(subs);
        }
        menuResps.sort(Comparator.comparing(menuResp -> Integer.valueOf(menuResp.getIndex())));
        return menuResps;
    }

    /**
     * 递归封装菜单树形结构
     *
     * @param parentId
     * @param menus
     * @return
     */
    private List<MenuResp> findSubs(Integer parentId, List<Menu> menus) {
        List<MenuResp> subs = Lists.newArrayList();
        for (Menu menu : menus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                List<MenuResp> menuResps = findSubs(menu.getId(), menus);
                MenuResp menuResp = MenuResp.builder().icon(menu.getMenuIcon())
                        .index(menu.getMenuIndex())
                        .title(menu.getTitle())
                        .id(menu.getId())
                        .label(menu.getTitle())
                        .shows(stringToList(menu.getRoles()))
                        .roleIds(stringToList(menu.getRoles()))
                        .subs(menuResps)
                        .children(menuResps)
                        .build();
                subs.add(menuResp);
            }
        }
        return subs;
    }


    public void addMenu(Menu menu) {
        if (Objects.nonNull(menu.getId())) {
            menuDAO.updateMenu(menu);
        } else {
            menuDAO.saveMenu(menu);
        }
    }

    public void updateMenu(Menu menu) {
        menuDAO.updateMenu(menu);
    }

    public void deleteMenu(Integer id) {
        menuDAO.deleteMenu(id);
    }

    public UserAuthResp hasAuth(String router, HttpServletRequest request, HttpServletResponse response) {
        boolean hasLoginAuth = userRoleService.checkLoginAuth(request, response);
        if (!hasLoginAuth) {
            return UserAuthResp.builder().hasLoginAuth(false).hasRouterAuth(false).build();
        }
        return UserAuthResp.builder().hasLoginAuth(true).hasRouterAuth(checkRouterAuth(router)).build();
    }

    public boolean checkRouterAuth(String router) {
        // 用户无任何角色权限
        List<Privilege> privileges = userRoleDAO.getPrivilegesByUserAndProductId(ParameterThreadLocal.getUserId(), ParameterThreadLocal.getProductId());
        if (isNull(privileges)) {
            return false;
        }
        List<Integer> roles = privileges.stream().map(Privilege::getRoleId).collect(Collectors.toList());
        Optional<Menu> optionalMenu = menuDAO.getMenu(router);
        return optionalMenu.map(menu -> this.hasRouterPrivilege(menu, roles)).orElse(false);
    }

    /**
     * 判断用户是否有访问当前页面的权限
     *
     * @param menu
     * @param roles
     * @return
     */
    private boolean hasRouterPrivilege(Menu menu, List<Integer> roles) {
        if (StringUtils.isEmpty(menu.getRoles())) {
            Menu parentMenu = menuDAO.getMenuByParent(menu.getParentId());
            return userRoleService.hasRole(roles, stringToList(parentMenu.getRoles()));
        }
        // 菜单路由，判断配置的角色与当前用户的所有角色列表是否匹配（取交集）
        return userRoleService.hasRole(roles, stringToList(menu.getRoles()));
    }


}
