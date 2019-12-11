package com.pr.sepp.mgr.role.service;

import java.util.List;
import java.util.Map;

import com.pr.sepp.mgr.role.model.Role;

public interface RoleService {

	List<Role> roleQuery(Map<String, Object> dataMap);

	List<Map<String, Object>> privQuery(Map<String, String> dataMap);

	int privUpdate(Map<String, Object> dataMap);

	int privDelete(Map<String, Object> dataMap);

	List<Map<String, Object>> productRoleQueryUser(Integer userId);
}
