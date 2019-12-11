package com.pr.sepp.mgr.user.service;

import com.pr.sepp.mgr.user.model.UserSetting;

public interface UserSettingService {
	UserSetting userSettingQuery(Integer userId);

	int userSettingUpdate(UserSetting userSetting);
}
