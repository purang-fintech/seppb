package com.pr.sepp.sep.device.service;

import java.util.List;
import java.util.Map;

import com.pr.sepp.sep.device.model.Device;

public interface DeviceService {
	public List<Device> deviceQuery(Map<String, String> dataMap);

	public int changeUser(Map<String, Object> dataMap);

	public int deviceUpdate(Device device);

	public int deviceCreate(Device device);

	public int intoWarehouse(Map<String, Object> dataMap);

}
