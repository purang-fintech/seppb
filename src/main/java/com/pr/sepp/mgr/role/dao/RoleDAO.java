package com.pr.sepp.mgr.role.dao;

import com.pr.sepp.mgr.role.model.Role;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RoleDAO {

	List<Role> roleQuery(Map<String, Object> dataMap);

	List<Map<String, Object>> privQuery(Map<String, String> dataMap);

	int privUpdate(Map<String, Object> dataMap);

	int privDelete(Map<String, Object> dataMap);

	List<Map<String, Object>> productRoleQueryUser(Integer userId);
}
