package com.pr.sepp.mgr.team.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.pr.sepp.common.constants.CommonParameter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pr.sepp.mgr.team.model.Organization;
import com.pr.sepp.mgr.team.service.OrganizationService;
import com.pr.sepp.mgr.user.model.User;

@RestController
@ResponseBody
public class OrganizationController {

	@Autowired
	private OrganizationService teamService;

	@RequestMapping(value = "/team/query", method =  RequestMethod.POST)
	public List<Organization> teamQuery(HttpServletRequest request) throws Exception {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("teamName", request.getParameter("teamName"));
		dataMap.put(CommonParameter.ID, request.getParameter(CommonParameter.ID));
		dataMap.put(CommonParameter.RESPONSER, request.getParameter(CommonParameter.RESPONSER));
		dataMap.put("parentId", request.getParameter("parentId"));

		return teamService.teamQuery(dataMap);
	}

	@RequestMapping(value = "/team/create", method =  RequestMethod.POST)
	public int teamCreate(HttpServletRequest request) {

		Organization team = new Organization();

		team.setTeamName(request.getParameter("teamName"));
		team.setTeamDescription(request.getParameter("teamDescription"));
		team.setParentId(Integer.parseInt(request.getParameter("parentId")));
		team.setResponser(Integer.parseInt(request.getParameter(CommonParameter.RESPONSER)));

		teamService.teamCreate(team);

		return team.getId();
	}

	@RequestMapping(value = "/team/update", method =  RequestMethod.POST)
	public int teamUpdate(HttpServletRequest request) {

		Organization team = new Organization();

		team.setTeamName(request.getParameter("teamName"));
		team.setId(Integer.parseInt(request.getParameter(CommonParameter.ID)));
		team.setTeamDescription(request.getParameter("teamDescription"));
		team.setParentId(StringUtils.isNotEmpty(request.getParameter("parentId")) ? Integer.parseInt(request.getParameter("parentId")) : null);
		team.setResponser(StringUtils.isNotEmpty(request.getParameter(CommonParameter.RESPONSER)) ? Integer.parseInt(request.getParameter(CommonParameter.RESPONSER)) : null);

		return teamService.teamUpdate(team);
	}

	@RequestMapping(value = "/team/delete", method =  RequestMethod.POST)
	public int teamDelete(@RequestParam(value = CommonParameter.ID, required = true) int id) {
		return teamService.teamDelete(id);
	}

	@RequestMapping(value = "/team/member_query", method =  RequestMethod.POST)
	public List<User> teamMemberQuery(@RequestParam(value = CommonParameter.ID, required = true) int id) throws Exception {

		return teamService.teamMemberQuery(id);
	}

	@RequestMapping(value = "/team/member_add", method =  RequestMethod.POST)
	public int teamMemberAdd(@RequestParam(value = CommonParameter.ID, required = true) int id, @RequestParam(value = "members", required = true) String members) throws Exception {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, id);
		dataMap.put("members", Arrays.asList(members.split(",")));

		return teamService.teamMemberAdd(dataMap);
	}

	@RequestMapping(value = "/team/member_remove", method =  RequestMethod.POST)
	public int teamMemberRemove(@RequestParam(value = "members", required = true) String members) throws Exception {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("members", Arrays.asList(members.split(",")));

		return teamService.teamMemberRemove(dataMap);
	}

}
