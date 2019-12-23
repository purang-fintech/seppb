package com.pr.sepp.mgr.role.service;

import java.util.List;
import java.util.Map;

public interface RoleService {

	List<Map<String, Object>> privQuery(Map<String, Object> dataMap);

	int privUpdate(Integer productId, Integer userId, List<String> roles);

	int privDelete(Integer userId, List<String> privIds);

	List<Map<String, Object>> productRoleQueryUser(Integer userId);
}
