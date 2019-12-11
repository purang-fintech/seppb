package com.pr.sepp.mgr.user.controller;

import com.pr.sepp.auth.core.jwt.Sessions;
import com.pr.sepp.auth.core.permission.annotation.ApiPermission;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.mgr.user.service.UserService;
import com.pr.sepp.utils.SHAEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@RestController
@ResponseBody
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	HistoryService historyService;

	@RequestMapping(value = "/user/query", method = RequestMethod.POST)
	public List<User> userQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("userAccount", request.getParameter("userAccount"));
		dataMap.put("userEmail", request.getParameter("userEmail"));
		dataMap.put("userName", request.getParameter("userName"));
		dataMap.put("isValid", request.getParameter("isValid"));
		dataMap.put("isVendor", request.getParameter("isVendor"));
		dataMap.put("userId", request.getParameter("userId"));

		List<User> users = userService.userQuery(dataMap);
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			user.setPassword(null);
			users.remove(i);
			users.add(i, user);
		}

		return users;
	}

	@RequestMapping(value = "/user/products", method = {RequestMethod.POST, RequestMethod.GET})
	public List<Map<String, Object>> userProductList() {
		return userService.userProductList();
	}

	@RequestMapping(value = "/user/user_queryIds", method = RequestMethod.POST)
	public List<User> userQueryByIds(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		String ids = request.getParameter("ids");
		if (!StringUtils.isEmpty(ids)) {
			dataMap.put("sts", Arrays.asList(ids.split(",")));
		} else {
			return null;
		}

		return userService.userQueryByIds(dataMap);
	}

	@RequestMapping(value = "/user/priv_apply", method = RequestMethod.POST)
	public List<String> userPrivApply(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("products", request.getParameter("products"));
		dataMap.put("roles", request.getParameter("roles"));
		dataMap.put("userId", request.getParameter("userId"));

		return userService.userPrivApply(dataMap);
	}

	@RequestMapping(value = "/user/create", method = RequestMethod.POST)
	public int userCreate(HttpServletRequest request) {

		User user = new User();

		user.setUserAccount(request.getParameter("userAccount"));
		user.setUserEmail(request.getParameter("userEmail"));
		user.setUserName(request.getParameter("userName"));
		user.setIsValid(request.getParameter("isValid"));
		user.setIsVendor(request.getParameter("isVendor"));

		String faviconId = request.getParameter("faviconId");
		if (StringUtils.isNotEmpty(faviconId)) {
			user.setFaviconId(Integer.parseInt(faviconId));
		}

		userService.userCreate(user);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(10);
		history.setObjId(user.getUserId());
		history.setObjKey("user_id");
		history.setProductId(Integer.parseInt(request.getParameter("productId")));
		history.setOperUser(Integer.parseInt(request.getParameter("userId")));
		history.setOperType(1);
		history.setOperComment("创建/注册新用户账户【" + request.getParameter("userAccount") + "】，用户姓名为【" + user.getUserName() + "】");
		history.setReferUser(user.getUserId());
		historyService.historyInsert(history);

		return user.getUserId();
	}

	@RequestMapping(value = "/user/update", method = RequestMethod.POST)
	public int userUpdate(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException {

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("userId", request.getParameter("referUser"));
		User oldUser = userService.userQuery(queryMap).get(0);

		User user = new User();

		user.setUserId(oldUser.getUserId());
		String newPassword = request.getParameter("newPwd");
		String oldPassword = request.getParameter("oldPwd");
		if (StringUtils.isNotEmpty(newPassword) && StringUtils.isNotEmpty(oldPassword)
				&& (int) userService.normalAuth(oldUser.getUserAccount(), oldPassword, response).get("result") == 1) {
			try {
				user.setPassword(SHAEncoder.encodeSHA256(newPassword.getBytes()));
			} catch (Exception e) {
				log.error("二次HASH出错！");
				e.printStackTrace();
			}
		}

		user.setUserAccount(request.getParameter("userAccount"));
		user.setUserEmail(request.getParameter("userEmail"));
		user.setUserName(request.getParameter("userName"));
		user.setIsValid(request.getParameter("isValid"));
		user.setIsVendor(request.getParameter("isVendor"));

		String faviconId = request.getParameter("faviconId");
		if (StringUtils.isNotEmpty(faviconId)) {
			user.setFaviconId(Integer.parseInt(faviconId));
		}

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends User> cls = user.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(user);
			Object oldValue = field.get(oldUser);

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(10);
				history.setObjId(oldUser.getUserId());
				history.setProductId(Integer.parseInt(request.getParameter("productId")));
				history.setOperUser(Integer.parseInt(request.getParameter("userId")));
				history.setOperType(2);
				history.setOperComment("用户【" + oldUser.getUserName() + "/" + oldUser.getUserAccount() + "】信息或个人设置变更");
				history.setReferUser(oldUser.getUserId());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return userService.userUpdate(user);
	}

	@RequestMapping(value = "/user/delete", method = RequestMethod.POST)
	public int userDelete(HttpServletRequest request) {
		int userId = Integer.parseInt(request.getParameter("referUser"));

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("userId", userId);
		User oldUser = userService.userQuery(queryMap).get(0);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(10);
		history.setObjId(userId);
		history.setObjKey("is_valid");
		history.setProductId(Integer.parseInt(request.getParameter("productId")));
		history.setOperUser(Integer.parseInt(request.getParameter("userId")));
		history.setOperType(3);
		history.setOperComment("删除/禁用用户【" + oldUser.getUserAccount() + "】的账户");
		history.setReferUser(userId);
		history.setOrgValue("Y");
		history.setNewValue("N");
		historyService.historyInsert(history);

		return userService.userDelete(userId);
	}

	@RequestMapping(value = "/user/query_p", method = RequestMethod.POST)
	public List<User> userQueryProduct() {
		return userService.userQueryProduct();
	}

	@RequestMapping(value = "/user/query_product/{productId}", method = RequestMethod.POST)
	public List<User> memberQuery(@PathVariable("productId") Integer productId) {
		return userService.memberQuery(productId);
	}

	@RequestMapping(value = "/user/query_p_r/{productId}/{roleId}", method = RequestMethod.POST)
	public List<User> userQueryProductRole(@PathVariable("productId") Integer productId, @PathVariable("roleId") Integer roleId) {
		return userService.userQueryProductRole(productId, roleId);
	}

	@ApiPermission
	@RequestMapping(value = "/user/ldap_auth", method = RequestMethod.POST)
	public Map<String, Object> ldapAuth(HttpServletResponse response, @RequestParam(value = "domain") String domain,
										@RequestParam(value = "password") String password, @RequestParam(value = "account") String account) {
		return userService.ldapAuth(domain, account, password, response);
	}

	@RequestMapping(value = "/user/list_domain", method = RequestMethod.POST)
	public List<String> getDomainList() {
		return userService.getDomainList();
	}

	@RequestMapping(value = "/user/normal_auth", method = RequestMethod.POST)
	public Map<String, Object> normalAuth(@RequestParam(value = "password") String password,
										  @RequestParam(value = "account") String account,
										  HttpServletResponse response) {
		return userService.normalAuth(account, password, response);
	}

	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	public int userRegister(HttpServletRequest request) {
		User user = new User();
		user.setUserAccount(request.getParameter("userAccount"));
		user.setPassword(request.getParameter("password"));
		user.setUserName(request.getParameter("userName"));
		user.setUserEmail(request.getParameter("userEmail"));

		return userService.userRegister(user);
	}

	@PostMapping("/user/logout")
	public void logout(HttpServletResponse response) {
		Sessions.logout(response);
	}
}
