package com.pr.sepp.sep.device.controller;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.device.model.Device;
import com.pr.sepp.sep.device.service.DeviceService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class DeviceController {
	@Autowired
	public DeviceService deviceService;

	@RequestMapping(value = "/device/query", method =  RequestMethod.POST)
	public PageInfo<Device> deviceQuery(HttpServletRequest request) {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("assetId", request.getParameter("assetId"));
		dataMap.put("userName", request.getParameter("userName"));
		dataMap.put("oprSys", request.getParameter("oprSys"));
		dataMap.put(CommonParameter.STATUS, request.getParameter(CommonParameter.STATUS));
		dataMap.put("brand", request.getParameter("brand"));
		dataMap.put("deviceName", request.getParameter("deviceName"));

		int pageNum = ParameterThreadLocal.getPageNum();
		int pageSize = ParameterThreadLocal.getPageSize();
		PageHelper.startPage(pageNum, pageSize);
		List<Device> list = deviceService.deviceQuery(dataMap);
		PageInfo<Device> pageInfo = new PageInfo<>(list);

		return pageInfo;

	}

	@RequestMapping(value = "/device/rent", method =  RequestMethod.POST)
	public int changeUser(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		String id = request.getParameter(CommonParameter.ID);
		dataMap.put(CommonParameter.ID, id);
		String userName = request.getParameter("userName");
		dataMap.put("userName", userName);
		String rentDate = request.getParameter("rentDate");
		dataMap.put("rentDate", rentDate);
		dataMap.put(CommonParameter.STATUS, "已借出");

		return deviceService.changeUser(dataMap);

	}

	@RequestMapping(value = "/device/return", method =  RequestMethod.POST)
	public int intoWarehouse(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		String id = request.getParameter(CommonParameter.ID);
		dataMap.put(CommonParameter.ID, id);
		dataMap.put("userName", "仓库");
		String returnDate = request.getParameter("returnDate");
		dataMap.put("returnDate", returnDate);
		dataMap.put(CommonParameter.STATUS, "已归还");
		return deviceService.intoWarehouse(dataMap);
	}

	@RequestMapping(value = "/device/create", method =  RequestMethod.POST)
	public int deviceCreate(HttpServletRequest request) {
		Device device = new Device();
		device.setUserName(request.getParameter("userName"));
		device.setAssetId(request.getParameter("assetId"));
		device.setDeviceName(request.getParameter("deviceName"));
		device.setBrand(request.getParameter("brand"));
		device.setOprSys(request.getParameter("oprSys"));
		device.setModel(request.getParameter("model"));
		device.setColor(request.getParameter("color"));
		device.setVersions(request.getParameter("versions"));
		device.setRam(Integer.parseInt(request.getParameter("ram")));
		device.setRom(Integer.parseInt(request.getParameter("rom")));
		return deviceService.deviceCreate(device);
	}

	@RequestMapping(value = "/device/update", method =  RequestMethod.POST)
	public int deviceUpdate(HttpServletRequest request) {
		Device device = new Device();
		device.setVersions(request.getParameter("versions"));
		device.setId(Integer.parseInt(request.getParameter(CommonParameter.ID)));
		return deviceService.deviceUpdate(device);
	}
}
