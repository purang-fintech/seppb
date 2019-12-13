package com.pr.sepp.mgr.user.dao;

import com.pr.sepp.mgr.user.model.UserSetting;

public interface UserSettingDAO {
	UserSetting userSettingQuery(Integer userId);

	int userSettingCreate(UserSetting userSetting);

	int userSettingUpdate(UserSetting userSetting);
}
