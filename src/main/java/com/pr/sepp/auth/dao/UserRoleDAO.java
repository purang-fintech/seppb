package com.pr.sepp.auth.dao;

import com.pr.sepp.auth.model.CurrentUserRole;
import com.pr.sepp.auth.model.Privilege;
import com.pr.sepp.auth.model.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRoleDAO {

    List<Role> allRoles(@Param("valid") String valid);

    List<Privilege> getPrivilegesByUserAndProductId(Integer userId, Integer productId);

    List<Role> getRolesByIds(@Param("roleIds") List<Integer> roleIds);

    void save(Role role);

    void setRoleValid(String isValid, Integer roleId);

    void updateRole(Role role);

    List<Role> findRolesByUser(Integer userId, Integer productId);

    List<CurrentUserRole> currentUserRoles(Integer userId);

    List<CurrentUserRole> currentUserRolesByProductId(Integer userId,Integer productId);
}
