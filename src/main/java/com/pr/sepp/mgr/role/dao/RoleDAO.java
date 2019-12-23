package com.pr.sepp.mgr.role.dao;

import java.util.List;
import java.util.Map;

public interface RoleDAO {

	List<Map<String, Object>> privQuery(Map<String, Object> dataMap);

	int privUpdate(Integer productId, Integer userId, List<String> roles);

	int privDelete(Integer userId, List<String> privIds);

	List<Map<String, Object>> productRoleQueryUser(Integer userId);
}
