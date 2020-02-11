package com.pr.sepp.mgr.role.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.role.service.RoleService;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.mgr.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@ResponseBody
public class RoleController {

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private HistoryService historyService;

	@RequestMapping(value = "/priv/query", method =  RequestMethod.POST)
	public PageInfo<Map<String, Object>> privQuery(HttpServletRequest request) throws Exception {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId()); //只允许当前项目
		dataMap.put(CommonParameter.USER_ID, request.getParameter(CommonParameter.USER_ID)); //可指定所有用户
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
		String[] roles = request.getParameter("roles").split(",");
		Integer userId = ParameterThreadLocal.getUserId();	//当前操作用户
		Integer productId = ParameterThreadLocal.getProductId();//只允许当前项目
		Integer privedUser = Integer.parseInt(request.getParameter("operUser")); //指定授权用户

		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.USER_ID, privedUser);
		User user = userService.userQuery(userMap).get(0);

		int count = roleService.privUpdate(productId, privedUser, Arrays.asList(roles));

		SEPPHistory history = new SEPPHistory();
		history.setObjType(13);
		history.setObjId(privedUser);
		history.setObjKey("priv_id");
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(2);
		history.setOperComment("为用户【" + user.getUserAccount() + "/" + user.getUserName() + "】增加了【" + count + "】条权限");
		history.setReferUser(privedUser);
		historyService.historyInsert(history);

		return count;
	}

	@RequestMapping(value = "/priv/delete", method =  RequestMethod.POST)
	public int privDelete(@RequestParam(value = "privId") String privIds) {
		List<String> privList = Arrays.asList(privIds.split(","));
		List<User> users = userService.distinctUsersByPrivIds(privList);
		Map<String, Integer> result = new HashMap<>();
		result.put("result", 0);

		users.forEach(user -> {
			int count = roleService.privDelete(user.getUserId(), privList);
			result.put("result", count + result.get("result"));

			SEPPHistory history = new SEPPHistory();
			history.setObjType(13);
			history.setObjId(user.getUserId());
			history.setObjKey("priv_id");
			history.setProductId(ParameterThreadLocal.getProductId());
			history.setOperUser(ParameterThreadLocal.getUserId());
			history.setOperType(3);
			history.setOperComment("删除了用户【" + user.getUserAccount() + "/" + user.getUserName() + "】的【" + count + "】条权限");
			history.setReferUser(user.getUserId());
			historyService.historyInsert(history);
		});

		return result.get("result");
	}

	@RequestMapping(value = "/role/p_r_query_user", method =  RequestMethod.POST)
	public List<Map<String, Object>> productRoleQueryUser(@RequestParam(value = CommonParameter.USER_ID) Integer userId) {
		return roleService.productRoleQueryUser(userId);
	}
}
