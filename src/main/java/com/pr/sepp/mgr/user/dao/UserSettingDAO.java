package com.pr.sepp.mgr.user.dao;

import com.pr.sepp.mgr.user.model.UserSetting;

public interface UserSettingDAO {
	UserSetting userSettingQuery(Integer userId);

	void userSettingCreate(int userId);

	int userSettingUpdate(UserSetting userSetting);
}
