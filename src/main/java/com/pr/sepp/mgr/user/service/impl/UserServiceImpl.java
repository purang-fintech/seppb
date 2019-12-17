package com.pr.sepp.mgr.user.service.impl;

import com.pr.sepp.auth.core.jwt.Sessions;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.exception.SeppServerException;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.role.dao.RoleDAO;
import com.pr.sepp.mgr.system.service.SettingService;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.mgr.user.dao.UserSettingDAO;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.mgr.user.model.UserSetting;
import com.pr.sepp.mgr.user.service.UserService;
import com.pr.sepp.notify.model.Message;
import com.pr.sepp.notify.service.MessageService;
import com.pr.sepp.utils.SHAEncoder;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.pr.sepp.common.constants.CommonParameter.USER_ID;
import static com.pr.sepp.common.constants.SeppConstants.*;
import static org.apache.commons.lang3.StringUtils.appendIfMissing;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Transactional
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private UserSettingDAO userSettingDAO;

	@Autowired
	private RoleDAO roleDAO;

	@Autowired
	private MessageService messageService;

	@Autowired
	private SettingService settingService;

	@Value("${login.signing-secret}")
	private String secret;

	@Override
	public List<User> userQuery(Map<String, Object> dataMap) {
		return userDAO.userQuery(dataMap);
	}

	@Override
	public int userCreate(User user) {
		userDAO.userCreate(user);
		int userId = user.getUserId();

		UserSetting setting = new UserSetting();
		setting.setUserId(userId);
		userSettingDAO.userSettingCreate(setting);

		return userId;
	}

	@Override
	public int userUpdate(User user) {
		return userDAO.userUpdate(user);
	}

	@Override
	public int userDelete(int userId) {
		return userDAO.userDelete(userId);
	}

	@Override
	public List<String> userPrivApply(Map<String, Object> dataMap) {
		User applyUser = userDAO.userQuery(dataMap).get(0);
		List<String> results = new ArrayList<>();

		final List<Map<String, String>> products = new Gson().fromJson(String.valueOf(dataMap.get("products")),
				new TypeToken<List<Map<String, String>>>() {
				}.getType());
		final List<Map<String, String>> roles = new Gson().fromJson(String.valueOf(dataMap.get("roles")),
				new TypeToken<List<Map<String, String>>>() {
				}.getType());

		StringBuffer roleList = new StringBuffer();
		roleList.append("申请权限清单：");
		for (int i = 0; i < roles.size(); i++) {
			roleList.append(roles.get(i).get("role") + (i == roles.size() - 1 ? "" : "; "));
		}

		products.forEach(item -> {
			Map<String, Object> adminMap = new HashMap<>();
			adminMap.put(CommonParameter.PRODUCT_ID, item.get(CommonParameter.ID));
			adminMap.put("roleId", "0");
			List<User> admins = userDAO.userQueryProductRole(adminMap);
			if (admins.size() == 0) {
				results.add("产品【" + item.get("product") + "】没有配置项目管理员！");
				return;
			}

			Message message = new Message();
			message.setProductId(Integer.parseInt(item.get(CommonParameter.ID)));
			message.setTitle("来自用户【" + applyUser.getUserAccount() + " - " + applyUser.getUserName() + "】的权限申请");
			message.setContent(roleList.toString());
			message.setObjectType(13);
			message.setObjectId(applyUser.getUserId());

			List<Integer> messageTo = new ArrayList<>();
			admins.forEach(admin -> {
				messageTo.add(admin.getUserId());
			});

			messageService.businessMessageGenerator(message, ParameterThreadLocal.getUserId(), messageTo);
		});

		return results;
	}

	@Override
	public List<User> userQueryProduct() {
		return userDAO.userQueryProduct(ParameterThreadLocal.getProductId());
	}

	@Override
	public List<User> userQueryProductRole(Integer productId, Integer roleId) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.PRODUCT_ID, productId);
		dataMap.put("roleId", roleId);
		return userDAO.userQueryProductRole(dataMap);
	}

	@Override
	public Map<String, Object> ldapAuth(String userDomain, String account, String password,
										HttpServletResponse response) {
		//获取ldap配置
		Map<String, String> settings = ldapSetting(userDomain);
		LdapContext ctx = null;
		Map<String, Object> result = Maps.newHashMap();
		try {
			ctx = new InitialLdapContext(buildEnvironment(account, password, settings), null);
			//获取用户的名称
			String userName = applyUserName(account, ctx);
			log.info("当前登录用户为:{}", userName);
			//封装用户登录返回结果
			result = buildLoginResult(account, settings.get("domain"), userName);
			Sessions.loginUser(((User) result.get(LOGIN_USER)).getUserId(), true, secret, response);
			return result;
		} catch (javax.naming.AuthenticationException e) {
			result.put(LOGIN_RESULT, 0);
			result.put(LOGIN_USER, null);
			log.error("用户名密码校验不通过:{}", e.getMessage());
		} catch (Exception e) {
			result.put(LOGIN_RESULT, -1);
			result.put(LOGIN_USER, null);
			log.error("用户账户状态或认证服务异常:{}", e.getMessage());
		} finally {
			ldapClose(ctx);
		}
		return result;
	}


	@Override
	public List<String> getDomainList() {
		List<String> domains = new ArrayList<>();
		List<Map<String, Object>> settings = settingService.listSetting();
		Map<String, Object> ldap = settings.stream().filter(f -> String.valueOf(f.get("settingName")).contains("LDAP")).findFirst().orElse(new HashMap<>());

		if (null == ldap) {
			log.error("没有正确配置后端LDAP服务：配置项为空！");
			throw new RuntimeException("没有正确配置后端LDAP服务：配置项为空！");
		}

		List<Map<String, String>> value = new Gson().fromJson(String.valueOf(ldap.get("settingValue")), new TypeToken<List<Map<String, String>>>() {
		}.getType());

		if (null == value || value.size() == 0) {
			log.error("没有正确配置后端LDAP服务：配置项为空！");
			throw new RuntimeException("没有正确配置后端LDAP服务：配置项为空！");
		}

		value.forEach(d -> domains.add(String.valueOf(d.get("domainName"))));

		return domains;
	}

	@Override
	public Map<String, Object> normalAuth(String account, String password, HttpServletResponse response) {

		List<User> users;
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		dataMap.put("userAccount", account);
		users = userDAO.userQuery(dataMap);
		if (users.size() == 0) {
			result.put("result", 0);
			result.put("user", null);
			return result;
		}
		try {
			String hashedTwice = SHAEncoder.encodeSHA256(password.getBytes());
			User authUser = users.get(0);

			if (hashedTwice.equalsIgnoreCase(authUser.getPassword())) {
				authUser.setPassword(null); // 密码怎可传到前端去~
				result.put("result", 1);
				result.put("user", authUser);
				Sessions.loginUser(authUser.getUserId(), true, secret, response);
			} else {
				result.put("result", 0);
				result.put("user", null);
			}
			return result;
		} catch (Exception e) {
			result.put("result", -1);
			result.put("user", null);
			log.error("二次HASH出错！", e);
		}
		return result;
	}

	@Override
	public int userRegister(User user) {
		String hashPassword = user.getPassword();

		try {
			hashPassword = SHAEncoder.encodeSHA256(hashPassword.getBytes());
		} catch (Exception e) {
			log.error("二次HASH出错！");
			e.printStackTrace();
		}

		User newUser = new User();
		newUser.setUserAccount(user.getUserAccount());
		newUser.setUserName(user.getUserName());
		newUser.setUserEmail(user.getUserEmail());
		newUser.setPassword(hashPassword);
		newUser.setIsValid("Y");
		newUser.setIsVendor("N");

		userDAO.userCreate(newUser);
		int userId = newUser.getUserId();

		UserSetting setting = new UserSetting();
		setting.setUserId(userId);
		userSettingDAO.userSettingCreate(setting);

		// 初始授权DEMO
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.USER_ID, userId);
		dataMap.put("products", Arrays.asList(new Integer[]{0}));
		dataMap.put("roles", Arrays.asList(new Integer[]{13}));
		roleDAO.privUpdate(dataMap);

		return userId;
	}

	@Override
	public List<User> userQueryByIds(Map<String, Object> dataMap) {
		return userDAO.userQueryByIds(dataMap);
	}

	@Override
	public List<User> memberQuery(Integer productId) {
		return userDAO.userQueryProduct(productId);
	}

	@Override
	public List<Map<String, Object>> userProductList() {
		return userDAO.userProductList(ParameterThreadLocal.getUserId());
	}

	/**
	 * 获取对应账户的用户名 （通过account获取userName）
	 * 该方法在用户名密码验证失败后会抛出异常，根据异常判断用户名密码是否正确，用户是否存在
	 *
	 * @param account
	 * @param ctx
	 * @return
	 * @throws NamingException
	 */
	private String applyUserName(String account, LdapContext ctx) throws NamingException {
		String userName = null;
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setReturningAttributes(new String[]{"uid", "userPassword", "displayName", "cn", "sn", "mail", "description"});
		String searchFilter = String.format(SEARCH_FILTER, account, account, account);
		NamingEnumeration<SearchResult> answer = ctx.search("DC=purang,DC=com", searchFilter, searchControls);
		while (answer.hasMoreElements()) {
			SearchResult sr = answer.next();
			String[] qResult = sr.getName().split(",");
			if (qResult.length > 1) {
				userName = qResult[0].split("=")[1];
			}
		}
		return userName;
	}

	/**
	 * 登录成功后封装用户信息
	 *
	 * @param account
	 * @param domain
	 * @param userName
	 */
	private Map<String, Object> buildLoginResult(String account, String domain, String userName) {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, Object> dataMap = Maps.newHashMap();
		dataMap.put("userAccount", account);
		List<User> users = userDAO.userQuery(dataMap);
		if (CollectionUtils.isEmpty(users)) {
			User newUser = User.builder().userAccount(account)
					.userName(defaultIfBlank(userName, account))
					.userEmail(account + "@" + domain)
					.isValid("Y")
					.isVendor("N").build();
			userDAO.userCreate(newUser);
			UserSetting setting = new UserSetting();
			setting.setUserId(newUser.getUserId());
			userSettingDAO.userSettingCreate(setting);
			dataMap.put(USER_ID, newUser.getUserId());
			users = userDAO.userQuery(dataMap);
		}
		result.put(LOGIN_RESULT, 1);
		result.put(LOGIN_USER, users.get(0));
		return result;
	}


	/**
	 * 该方法仅用于封装ldap账户的基本环境信息
	 *
	 * @param account
	 * @param password
	 * @param settings(@需要两个参数：ldapUrl和domain)
	 * @return
	 */
	private Properties buildEnvironment(String account, String password, Map<String, String> settings) {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
		env.put(Context.PROVIDER_URL, settings.get("ldapUrl"));
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, account + "@" + settings.get("domain"));
		env.put(Context.SECURITY_CREDENTIALS, password);
		return env;
	}

	/**
	 * 获取ldap的url和domain
	 *
	 * @param userDomain
	 * @return
	 */
	private Map<String, String> ldapSetting(String userDomain) {
		List<Map<String, String>> value = ldapSetting();
		Map<String, String> current = value.stream().filter(f -> f.get("domainName").equalsIgnoreCase(userDomain)).findFirst().orElse(new HashMap<>());
		String ldapUrl = String.format(LDAP_URL, String.valueOf(current.get("ldapServer")), String.valueOf(current.get("ldapPort")));
		String domain = appendIfMissing(userDomain.toUpperCase(), ".COM");
		Map<String, String> map = Maps.newHashMap();
		map.put("ldapUrl", ldapUrl);
		map.put("domain", domain);
		return map;
	}

	private List<Map<String, String>> ldapSetting() {
		List<Map<String, Object>> settings = settingService.listSetting();
		if (isEmpty(settings)) {
			throw new SeppServerException(GLOBAL_ERROR_CODE, NON_LDAP_ERROR_MESSAGE);
		}
		Map<String, Object> ldap = settings.stream().filter(s -> String.valueOf(s.get("settingName")).contains("LDAP")).findFirst().orElse(new HashMap<>());
		List<Map<String, String>> settingValues = new Gson().fromJson(String.valueOf(ldap.get("settingValue")), new TypeToken<List<Map<String, String>>>() {
		}.getType());
		if (isEmpty(settingValues)) {
			throw new SeppServerException(GLOBAL_ERROR_CODE, NON_LDAP_ERROR_MESSAGE);
		}
		return settingValues;
	}

	private void ldapClose(LdapContext ctx) {
		if (null != ctx) {
			try {
				ctx.close();
			} catch (NamingException e) {
				log.error("认证服务关闭异常", e.getMessage());
			}
		}
	}
}
