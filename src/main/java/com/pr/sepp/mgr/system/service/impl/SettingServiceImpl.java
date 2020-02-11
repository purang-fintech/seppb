package com.pr.sepp.mgr.system.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.mgr.system.model.SettingConfig;
import com.pr.sepp.mgr.system.model.SystemSetting;
import com.pr.sepp.mgr.system.service.SettingService;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Service("settingService")
public class SettingServiceImpl implements SettingService {
	@Autowired
	private SettingDAO settingDAO;
	@Autowired
	private JenkinsClientProvider jenkinsClientProvider;

	@Override
	public List<Map<String, Object>> listConfig() {

		List<Map<String, Object>> configs = settingDAO.listConfig();

		for (int i = 0; i < configs.size(); i++) {
			Map<String, Object> itemMap = configs.get(i);
			List<Map<String, Object>> settingKeys = new Gson().fromJson(String.valueOf(itemMap.get("settingKeys")),
					new TypeToken<List<Map<String, Object>>>() {
					}.getType());
			itemMap.put("settingKeys", settingKeys);
			configs.remove(i);
			configs.add(i, itemMap);
		}

		return configs;
	}

	/**
	 * 此处记得前端调用之后手动修改com.pr.sepp.mgr.system.constants.SettingType
	 * 增加一条枚举类型，再对应增加配置解析的程序入口……所以说前端的动态配置其实就是个摆设
	 * @param config
	 * @return
	 */
	@Override
	public int configCreate(SettingConfig config) {
		settingDAO.configCreate(config);
		return config.getId();
	}

	@Override
	public int configUpdate(SettingConfig config) {
		return settingDAO.configUpdate(config);
	}

	@Override
	public int configDelete(int configId) {
		return settingDAO.configDelete(configId);
	}

	@Override
	public List<Map<String, Object>> listSetting() {
		List<Map<String, Object>> settings = settingDAO.listSetting();

		for (int i = 0; i < settings.size(); i++) {
			Map<String, Object> itemMap = settings.get(i);
			List<Map<String, Object>> settingValue = new Gson().fromJson(String.valueOf(itemMap.get("settingValue")),
					new TypeToken<List<Map<String, Object>>>() {
					}.getType());
			itemMap.put("settingValue", settingValue);
			settings.remove(i);
			settings.add(i, itemMap);
		}

		return settings;
	}

	@Override
	public int settingCreate(SystemSetting setting) {
        int i = settingDAO.settingCreate(setting);
        jenkinsClientProvider.dynamicUpdateJenkinsClient();
        return i;
    }

	@Override
	public int settingUpdate(SystemSetting setting) {
        int i = settingDAO.settingUpdate(setting);
        jenkinsClientProvider.dynamicUpdateJenkinsClient();
        return i;
    }

	@Override
	public int settingDelete(int settingId) {
        int i = settingDAO.settingDelete(settingId);
        jenkinsClientProvider.dynamicUpdateJenkinsClient();
        return i;
    }

}