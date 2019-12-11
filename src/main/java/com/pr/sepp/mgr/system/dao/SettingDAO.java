package com.pr.sepp.mgr.system.dao;

import java.util.List;
import java.util.Map;
import com.pr.sepp.mgr.system.model.SettingConfig;
import com.pr.sepp.mgr.system.model.SystemSetting;
import com.pr.sepp.utils.jenkins.model.SettingType;

public interface SettingDAO {
	public List<Map<String, Object>> listConfig();

	public int configCreate(SettingConfig config);

	public int configUpdate(SettingConfig config);

	public int configDelete(int configId);

	public List<Map<String, Object>> listSetting();

	public int settingCreate(SystemSetting settting);

	public int settingUpdate(SystemSetting settting);

	public int settingDelete(int setttingId);

	SystemSetting findSetting(SettingType settingType);
}