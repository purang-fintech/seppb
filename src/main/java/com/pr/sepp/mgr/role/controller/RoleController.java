package com.pr.sepp.mgr.role.controller;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.role.model.Role;
import com.pr.sepp.mgr.role.service.RoleService;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.mgr.user.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class RoleController {
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserService userService;
	@Autowired
	private HistoryService historyService;

	@RequestMapping(value = "/role/query", method =  RequestMethod.POST)
	public PageInfo<Role> roleQuery(HttpServletRequest request) throws Exception {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("roleId", request.getParameter("roleId"));
		dataMap.put("roleCode", request.getParameter("roleCode"));
		dataMap.put("roleName", request.getParameter("roleName"));
		dataMap.put("isValid", request.getParameter("isValid"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<Role> list = roleService.roleQuery(dataMap);
		PageInfo<Role> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/priv/query", method =  RequestMethod.POST)
	public PageInfo<Map<String, Object>> privQuery(HttpServletRequest request) throws Exception {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, request.getParameter(CommonParameter.PRODUCT_ID));
		dataMap.put(CommonParameter.USER_ID, request.getParameter(CommonParameter.USER_ID));
		dataMap.put("roleId", request.getParameter("roleId"));
		dataMap.put("privId", request.getParameter("privId"));
		dataMap.put("isValid", request.getParameter("isValid"));

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		List<Map<String, Object>> list = roleService.privQuery(dataMap);
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@RequestMapping(value = "/priv/update", method =  RequestMethod.POST)
	public int privUpdate(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		String[] products = request.getParameter("products").split(",");
		String[] roles = request.getParameter("roles").split(",");
		dataMap.put(CommonParameter.USER_ID, request.getParameter("operUser"));
		dataMap.put("products", Arrays.asList(products));
		dataMap.put("roles", Arrays.asList(roles));

		int privedUser = Integer.parseInt(request.getParameter("operUser"));

		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.USER_ID, privedUser);
		User user = userService.userQuery(userMap).get(0);

		int count = roleService.privUpdate(dataMap);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(13);
		history.setObjId(privedUser);
		history.setObjKey("priv_id");
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(2);
		history.setOperComment("为用户【" + user.getUserAccount() + "/" + user.getUserName() + "】增加了来自【" + products.length + "】个产品、【"
				+ roles.length + "】个角色的【" + count + "】条权限");
		history.setReferUser(privedUser);
		historyService.historyInsert(history);

		return count;
	}

	@RequestMapping(value = "/priv/delete", method =  RequestMethod.POST)
	public int privDelete(HttpServletRequest request) {
		String privs = request.getParameter("privId");
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("privId", Arrays.asList(privs.split(",")));
		
		Map<String, String> queryMap = new HashMap<>();
		queryMap.put("privId", privs.split(",")[0]);
		Map<String, Object> privilege = roleService.privQuery(queryMap).get(0);
		int privedUser = (int) privilege.get(CommonParameter.USER_ID);

		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.USER_ID, privedUser);
		User user = userService.userQuery(userMap).get(0);
		
		int count = roleService.privDelete(dataMap);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(13);
		history.setObjId(privedUser);
		history.setObjKey("priv_id");
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(3);
		history.setOperComment("删除了用户【" + user.getUserAccount() + "/" + user.getUserName() + "】的【" + count + "】条权限");
		history.setReferUser(privedUser);
		historyService.historyInsert(history);

		return count;
	}

	@RequestMapping(value = "/role/p_r_query_user", method =  RequestMethod.POST)
	public List<Map<String, Object>> productRoleQueryUser(@RequestParam(value = CommonParameter.USER_ID) Integer userId) {
		return roleService.productRoleQueryUser(userId);
	}
}
