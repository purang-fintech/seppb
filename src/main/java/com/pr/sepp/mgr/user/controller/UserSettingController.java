package com.pr.sepp.mgr.user.controller;

import com.alibaba.fastjson.JSON;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.mgr.user.model.UserSetting;
import com.pr.sepp.mgr.user.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
public class UserSettingController {

	@Autowired
	private UserSettingService userSettingService;

	@RequestMapping(value = "/user/setting/query/{userId}", method = RequestMethod.POST)
	public UserSetting userSettingQuery(@PathVariable(CommonParameter.USER_ID) Integer userId) {
		return userSettingService.userSettingQuery(userId);
	}

	@RequestMapping(value = "/user/setting/update", method = RequestMethod.POST)
	public int userSettingUpdate(@RequestBody UserSetting userSetting) {
		return userSettingService.userSettingUpdate(userSetting);
	}
}
