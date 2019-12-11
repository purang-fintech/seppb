package com.pr.sepp.mgr.role.service.impl;

import com.pr.sepp.mgr.role.dao.RoleDAO;
import com.pr.sepp.mgr.role.model.Role;
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
	public List<Role> roleQuery(Map<String, Object> dataMap) {
		return roleDAO.roleQuery(dataMap);
	}

	@Override
	public List<Map<String, Object>> privQuery(Map<String, String> dataMap) {
		return roleDAO.privQuery(dataMap);
	}

	@Override
	public int privUpdate(Map<String, Object> dataMap) {
		return roleDAO.privUpdate(dataMap);
	}

	@Override
	public int privDelete(Map<String, Object> dataMap) {
		return roleDAO.privDelete(dataMap);
	}

	@Override
	public List<Map<String, Object>> productRoleQueryUser(Integer userId) {
		return roleDAO.productRoleQueryUser(userId);
	}
}
