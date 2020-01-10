package com.pr.sepp.mgr.team.dao;

import com.pr.sepp.mgr.team.model.Organization;
import com.pr.sepp.mgr.user.model.User;

import java.util.List;
import java.util.Map;

public interface OrganizationDAO {

	public List<Organization> teamQuery(Map<String, Object> dataMap);

	public int teamCreate(Organization team);

	public int teamUpdate(Organization team);

	public int teamDelete(int teamId);

	public List<User> teamMemberQuery(int teamId);

	public int teamMemberAdd(Map<String, Object> dataMap);

	public int teamMemberRemove(Map<String, Object> dataMap);

}
