package com.pr.sepp.mgr.user.service.impl;

import com.pr.sepp.mgr.user.dao.UserSettingDAO;
import com.pr.sepp.mgr.user.model.UserSetting;
import com.pr.sepp.mgr.user.service.UserSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@Slf4j
public class UserSettingServiceImpl implements UserSettingService {

	@Autowired
	private UserSettingDAO userSettingDAO;

	@Override
	public UserSetting userSettingQuery(Integer userId) {
		return userSettingDAO.userSettingQuery(userId);
	}

	@Override
	public int userSettingUpdate(UserSetting userSetting) {
		return userSettingDAO.userSettingUpdate(userSetting);
	}

}
