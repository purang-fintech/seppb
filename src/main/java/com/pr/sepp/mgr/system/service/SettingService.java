package com.pr.sepp.mgr.system.service;

import java.util.List;
import java.util.Map;
import com.pr.sepp.mgr.system.model.SettingConfig;
import com.pr.sepp.mgr.system.model.SystemSetting;

public interface SettingService {
	public List<Map<String, Object>> listConfig();

	public int configCreate(SettingConfig setting);

	public int configUpdate(SettingConfig setting);

	public int configDelete(int configId);

	public List<Map<String, Object>> listSetting();

	public int settingCreate(SystemSetting setting);

	public int settingUpdate(SystemSetting setting);

	public int settingDelete(int settingId);
}