package com.pr.sepp.mgr.user.dao;

import com.pr.sepp.mgr.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserDAO {

    List<User> userQuery(Map<String, Object> dataMap);

	int userCreate(User user);

    int userAccountExists(String userAccount);

	int userEmailExists(String userEmail);

	int userNameExists(String userName);

    int userUpdate(User user);

    int userDelete(int userId);

    List<User> userQueryProductRole(Map<String, Object> dataMap);

    List<User> userQueryProduct(Integer productId);

	User findUserByUserId(Integer userId);

    List<User> userQueryByIds(Map<String, Object> dataMap);

    List<Map<String, Object>> userProductList(int userId);

	List<User> distinctUsersByPrivIds(List<String> privList);
}
