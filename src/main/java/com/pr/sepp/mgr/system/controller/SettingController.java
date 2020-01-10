package com.pr.sepp.mgr.system.controller;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.system.model.SettingConfig;
import com.pr.sepp.mgr.system.model.SystemSetting;
import com.pr.sepp.mgr.system.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class SettingController {
	@Autowired
	private SettingService settingService;

	@RequestMapping(value = "/config/query", method =  RequestMethod.POST)
	public List<Map<String, Object>> listConfig() {
		return settingService.listConfig();
	}

	@RequestMapping(value = "/config/create", method =  RequestMethod.POST)
	public int configCreate(HttpServletRequest request) {
		SettingConfig config = new SettingConfig(); 
		
		config.setSettingName(request.getParameter("settingName"));
		config.setSettingLimit( Integer.parseInt(request.getParameter("settingLimit")));
		config.setSettingKeys(request.getParameter("settingKeys"));
		settingService.configCreate(config);
		
		return config.getId();
	}

	@RequestMapping(value = "/config/update", method =  RequestMethod.POST)
	public int configUpdate(HttpServletRequest request) {
		SettingConfig config = new SettingConfig(); 
		
		config.setSettingName(request.getParameter("settingName"));
		config.setSettingLimit( Integer.parseInt(request.getParameter("settingLimit")));
		config.setSettingKeys(request.getParameter("settingKeys"));
		config.setId(Integer.parseInt(request.getParameter(CommonParameter.ID)));
		
		return settingService.configUpdate(config);
	}

	@RequestMapping(value = "/config/delete", method =  RequestMethod.POST)
	public int configDelete(HttpServletRequest request) {
		return settingService.configDelete(Integer.parseInt(request.getParameter(CommonParameter.ID)));
	}

	@RequestMapping(value = "/setting/query", method =  RequestMethod.POST)
	public List<Map<String, Object>> listSetting() {
		return settingService.listSetting();
	}

	@RequestMapping(value = "/setting/create", method =  RequestMethod.POST)
	public int settingCreate(HttpServletRequest request) {
		SystemSetting setting = new SystemSetting(); 
		
		setting.setSettingType(Integer.parseInt(request.getParameter("settingType")));
		setting.setCreateUser(ParameterThreadLocal.getUserId());
		setting.setSettingValue(request.getParameter("settingValue"));
		settingService.settingCreate(setting);
		
		return setting.getId();
	}

	@RequestMapping(value = "/setting/update", method =  RequestMethod.POST)
	public int settingUpdate(HttpServletRequest request) {
		SystemSetting setting = new SystemSetting(); 
		setting.setSettingValue(request.getParameter("settingValue"));
		setting.setId(Integer.parseInt(request.getParameter(CommonParameter.ID)));
		
		return settingService.settingUpdate(setting);
	}

	@RequestMapping(value = "/setting/delete", method =  RequestMethod.POST)
	public int settingDelete(HttpServletRequest request) {
		return settingService.settingDelete(Integer.parseInt(request.getParameter(CommonParameter.ID)));
	}
}
