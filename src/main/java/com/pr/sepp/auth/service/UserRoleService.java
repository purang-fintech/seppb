package com.pr.sepp.auth.service;

import com.pr.sepp.auth.core.jwt.Sessions;
import com.pr.sepp.auth.dao.UserRoleDAO;
import com.pr.sepp.auth.model.*;
import com.pr.sepp.auth.model.resp.CurrentUserRoleResp;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pr.sepp.auth.core.jwt.Sessions.getSession;
import static com.pr.sepp.auth.model.Menu.stringToList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleDAO userRoleDAO;
    @Value("${login.signing-secret}")
    private String secret;

    public PageInfo<Role> allRoles(String valid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> roles = allRoles(valid);
        PageInfo<Role> pageInfo = new PageInfo<>(roles);
        return pageInfo;
    }

    public List<Role> allRoles(String valid) {
        return userRoleDAO.allRoles(valid);
    }

    public List<Privilege> getPrivilegesByUserAndProductId(Integer userId, Integer productId) {
        return userRoleDAO.getPrivilegesByUserAndProductId(userId, productId);
    }

    public void save(Role role) {
        if (nonNull(role.getRoleId())) {
            userRoleDAO.updateRole(role);
        } else {
            userRoleDAO.save(role);
        }
    }

    public void deleteRole(Integer roleId) {
        userRoleDAO.setRoleValid("N", roleId);
    }

    public CurrentUserRoleResp rolesByUser(Integer userId, Integer productId) {
        List<Role> roles = userRoleDAO.findRolesByUser(userId, productId);
        List<String> roleNames = roles.stream().map(Role::getRoleName).collect(toList());
        List<Integer> roleIds = roles.stream().map(Role::getRoleId).collect(toList());
        return CurrentUserRoleResp.builder()
                .roleIds(roleIds)
                .roleNames(roleNames)
                .build();
    }

    public CurrentUserRoleResp currentUserRoles(Integer userId) {
        List<CurrentUserRole> currentUserRoles = userRoleDAO.currentUserRoles(userId);
        Map<Integer, List<Integer>> productRoleIds = currentUserRoles.stream()
                .collect(groupingBy(CurrentUserRole::getProductId, Collectors.mapping(CurrentUserRole::getRoleId, toList())));
        Map<Integer, List<String>> productRoleCodes = currentUserRoles.stream()
                .collect(groupingBy(CurrentUserRole::getProductId, Collectors.mapping(CurrentUserRole::getRoleCode, toList())));
        return CurrentUserRoleResp.builder()
                .productRoleIds(productRoleIds)
                .productRoleCodes(productRoleCodes)
                .build();
    }

    public CurrentUserRoleResp currentUserRolesByProductId(Integer userId, Integer productId) {
        List<CurrentUserRole> currentUserRoles = userRoleDAO.currentUserRolesByProductId(userId, productId);
        return CurrentUserRoleResp.buildSimpleResp(currentUserRoles);
    }


    /**
     * 判断用户登录权限
     *
     * @return
     */
    public boolean checkLoginAuth(HttpServletRequest request, HttpServletResponse response) {
        Session session = getSession(request, secret);
        if (nonNull(session)) {
            Sessions.loginUser(session.getUserId(), session.isSupport(), secret, response);
        }
        return nonNull(session) && Objects.equals(session.getUserId(), ParameterThreadLocal.getUserId());
    }

    /**
     * 判断当前用户的角色是否在配置菜单的角色中
     *
     * @param roles
     * @return
     */
    public boolean hasRole(List<Integer> roles, List<Integer> routerRoleIds) {
        if (isNull(routerRoleIds)) {
            return false;
        }
        if (routerRoleIds.contains(-1)) {
            return true;
        }
        routerRoleIds.retainAll(roles);
        return CollectionUtils.isNotEmpty(routerRoleIds);
    }
}
