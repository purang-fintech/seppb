package com.pr.sepp.mgr.team.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pr.sepp.mgr.team.dao.OrganizationDAO;
import com.pr.sepp.mgr.team.model.Organization;
import com.pr.sepp.mgr.team.service.OrganizationService;
import com.pr.sepp.mgr.user.model.User;

@Transactional
@Service("organizationService")
public class OrganizationServiceServiceImpl implements OrganizationService {

	@Autowired
	private OrganizationDAO organizationDAO;
	
	@Override
	public List<Organization> teamQuery(Map<String, Object> dataMap) {
		return organizationDAO.teamQuery(dataMap);
	}

	@Override
	public int teamCreate(Organization team) {
		return organizationDAO.teamCreate(team);
	}

	@Override
	public int teamUpdate(Organization team) {
		return organizationDAO.teamUpdate(team);
	}

	@Override
	public int teamDelete(int teamId) {
		return organizationDAO.teamDelete(teamId);
	}

	@Override
	public List<User> teamMemberQuery(int teamId) {
		return organizationDAO.teamMemberQuery(teamId);
	}

	@Override
	public int teamMemberAdd(Map<String, Object> dataMap) {
		return organizationDAO.teamMemberAdd(dataMap);
	}

	@Override
	public int teamMemberRemove(Map<String, Object> dataMap) {
		return organizationDAO.teamMemberRemove(dataMap);
	}

}
