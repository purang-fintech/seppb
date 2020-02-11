package com.pr.sepp.mgr.user.service;

import com.pr.sepp.mgr.user.model.User;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface UserService {

	List<User> userQuery(Map<String, Object> dataMap);

	int userAccountExists(String userAccount);

	int userEmailExists(String userEmail);

	int userNameExists(String userName);

	int idUserEmailExists(Integer userId, String userEmail);

	int idUserNameExists(Integer userId, String userName);

	int userCreate(User user);

	int userUpdate(User user);

	int userDelete(int userId);

	List<String> userPrivApply(Map<String, Object> dataMap);

	List<User> userQueryProductRole(Integer productId, Integer roleId);

	List<User> userQueryProduct();

	List<String> getDomainList();
	
	Map<String, Object> ldapAuth(String userDomain, String account, String password, HttpServletResponse response);
	
	Map<String, Object> normalAuth(String account, String password,HttpServletResponse response);
	
	int userRegister(User user);

    List<User> userQueryByIds(Map<String, Object> dataMap);

	List<User> memberQuery(Integer productId);

	List<Map<String, Object>> userProductList();

	List<User> distinctUsersByPrivIds(List<String> privList);
}
