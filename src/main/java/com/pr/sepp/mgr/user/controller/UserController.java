package com.pr.sepp.mgr.user.controller;

import com.pr.sepp.auth.core.jwt.Sessions;
import com.pr.sepp.auth.core.permission.annotation.ApiPermission;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.mgr.user.service.UserService;
import com.pr.sepp.utils.SHAEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
	
	private static final String ACCOUT_KEY = "userAccount";
	private static final String EMAIL_KEY = "userEmail";
	private static final String UNAME_KEY = "userName";
	private static final String ISVALID_KEY = "isValid";
	private static final String ISVENDOR_KEY = "isVendor";

	@PostMapping(value = "/user/query")
	public List<User> userQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(ACCOUT_KEY, request.getParameter(ACCOUT_KEY));
		dataMap.put(EMAIL_KEY, request.getParameter(EMAIL_KEY));
		dataMap.put(UNAME_KEY, request.getParameter(UNAME_KEY));
		dataMap.put(ISVALID_KEY, request.getParameter(ISVALID_KEY));
		dataMap.put(ISVENDOR_KEY, request.getParameter(ISVENDOR_KEY));
		dataMap.put(CommonParameter.USER_ID, request.getParameter(CommonParameter.USER_ID));

		List<User> users = userService.userQuery(dataMap);
		users.forEach(user -> {
			user.setPassword(null);
		});

		return users;
	}

	@PostMapping(value = "/user/products")
	public List<Map<String, Object>> userProductList() {
		return userService.userProductList();
	}

	@PostMapping(value = "/user/user_queryIds")
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

	@PostMapping(value = "/user/priv_apply")
	public List<String> userPrivApply(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("products", request.getParameter("products"));
		dataMap.put("roles", request.getParameter("roles"));
		dataMap.put(CommonParameter.USER_ID, request.getParameter(CommonParameter.USER_ID));

		return userService.userPrivApply(dataMap);
	}

	@PostMapping(value = "/user/create")
	public int userCreate(HttpServletRequest request) {

		User user = new User();

		user.setUserAccount(request.getParameter(ACCOUT_KEY));
		user.setUserEmail(request.getParameter(EMAIL_KEY));
		user.setUserName(request.getParameter(UNAME_KEY));
		user.setIsValid(request.getParameter(ISVALID_KEY));
		user.setIsVendor(request.getParameter(ISVENDOR_KEY));

		String faviconId = request.getParameter("faviconId");
		if (StringUtils.isNotEmpty(faviconId)) {
			user.setFaviconId(Integer.parseInt(faviconId));
		}

		userService.userCreate(user);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(10);
		history.setObjId(user.getUserId());
		history.setObjKey("user_id");
		history.setProductId(Integer.parseInt(request.getParameter(CommonParameter.PRODUCT_ID)));
		history.setOperUser(Integer.parseInt(request.getParameter(CommonParameter.USER_ID)));
		history.setOperType(1);
		history.setOperComment("创建/注册新用户账户【" + request.getParameter(ACCOUT_KEY) + "】，用户姓名为【" + user.getUserName() + "】");
		history.setReferUser(user.getUserId());
		historyService.historyInsert(history);

		return user.getUserId();
	}

	@PostMapping(value = "/user/update")
	public int userUpdate(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException {

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(CommonParameter.USER_ID, request.getParameter("referUser"));
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
				log.error("二次HASH出错！", e);
			}
		}

		user.setUserAccount(request.getParameter(ACCOUT_KEY));
		user.setUserEmail(request.getParameter(EMAIL_KEY));
		user.setUserName(request.getParameter(UNAME_KEY));
		user.setIsValid(request.getParameter(ISVALID_KEY));
		user.setIsVendor(request.getParameter(ISVENDOR_KEY));

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
				history.setProductId(Integer.parseInt(request.getParameter(CommonParameter.PRODUCT_ID)));
				history.setOperUser(Integer.parseInt(request.getParameter(CommonParameter.USER_ID)));
				history.setOperType(2);
				history.setOperComment("用户【" + oldUser.getUserName() + "/" + oldUser.getUserAccount() + "】信息或个人设置变更");
				history.setReferUser(oldUser.getUserId());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (CollectionUtils.isEmpty(histories)) {
			historyService.historyInsertBatch(histories);
		}

		return userService.userUpdate(user);
	}

	@PostMapping(value = "/user/delete")
	public int userDelete(HttpServletRequest request) {
		int userId = Integer.parseInt(request.getParameter("referUser"));

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(CommonParameter.USER_ID, userId);
		User oldUser = userService.userQuery(queryMap).get(0);

		SEPPHistory history = new SEPPHistory();
		history.setObjType(10);
		history.setObjId(userId);
		history.setObjKey("is_valid");
		history.setProductId(Integer.parseInt(request.getParameter(CommonParameter.PRODUCT_ID)));
		history.setOperUser(Integer.parseInt(request.getParameter(CommonParameter.USER_ID)));
		history.setOperType(3);
		history.setOperComment("删除/禁用用户【" + oldUser.getUserAccount() + "】的账户");
		history.setReferUser(userId);
		history.setOrgValue("Y");
		history.setNewValue("N");
		historyService.historyInsert(history);

		return userService.userDelete(userId);
	}

	@PostMapping(value = "/user/query_p")
	public List<User> userQueryProduct() {
		return userService.userQueryProduct();
	}

	@PostMapping(value = "/user/query_product/{productId}")
	public List<User> memberQuery(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId) {
		return userService.memberQuery(productId);
	}

	@PostMapping(value = "/user/query_p_r/{productId}/{roleId}")
	public List<User> userQueryProductRole(@PathVariable(CommonParameter.PRODUCT_ID) Integer productId, @PathVariable("roleId") Integer roleId) {
		return userService.userQueryProductRole(productId, roleId);
	}

	@ApiPermission
	@PostMapping(value = "/user/ldap_auth")
	public Map<String, Object> ldapAuth(HttpServletResponse response, @RequestParam(value = "domain") String domain,
										@RequestParam(value = "password") String password, @RequestParam(value = "account") String account) {
		return userService.ldapAuth(domain, account, password, response);
	}

	@PostMapping(value = "/user/list_domain")
	public List<String> getDomainList() {
		return userService.getDomainList();
	}

	@PostMapping(value = "/user/normal_auth")
	public Map<String, Object> normalAuth(@RequestParam(value = "password") String password,
										  @RequestParam(value = "account") String account,
										  HttpServletResponse response) {
		return userService.normalAuth(account, password, response);
	}

	@PostMapping(value = "/user/register")
	public int userRegister(HttpServletRequest request) {
		User user = new User();
		user.setUserAccount(request.getParameter(ACCOUT_KEY));
		user.setPassword(request.getParameter("password"));
		user.setUserName(request.getParameter(UNAME_KEY));
		user.setUserEmail(request.getParameter(EMAIL_KEY));

		return userService.userRegister(user);
	}

	@PostMapping("/user/logout")
	public void logout(HttpServletResponse response) {
		Sessions.logout(response);
	}
}
