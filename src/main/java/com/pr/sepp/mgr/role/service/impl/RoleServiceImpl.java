package com.pr.sepp.mgr.role.service.impl;

import com.pr.sepp.mgr.role.dao.RoleDAO;
import com.pr.sepp.mgr.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Service("roleService")
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDAO roleDAO;

	@Override
	public List<Map<String, Object>> privQuery(Map<String, Object> dataMap) {
		return roleDAO.privQuery(dataMap);
	}

	@Override
	public int privUpdate(Integer productId, Integer userId, List<String> roles) {
		return roleDAO.privUpdate(productId, userId, roles);
	}

	@Override
	public int privDelete(Integer userId, List<String> privIds) {
		return roleDAO.privDelete(userId, privIds);
	}

	@Override
	public List<Map<String, Object>> productRoleQueryUser(Integer userId) {
		return roleDAO.productRoleQueryUser(userId);
	}
}
