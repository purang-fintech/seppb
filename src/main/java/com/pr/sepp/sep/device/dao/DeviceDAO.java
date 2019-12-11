package com.pr.sepp.sep.device.dao;

import com.pr.sepp.sep.device.model.Device;

import java.util.List;
import java.util.Map;

public interface DeviceDAO {
	List<Device> deviceQuery(Map<String, String> dataMap);

	int changeUser(Map<String, Object> dataMap);

	int deviceCreate(Device device);

	int deviceUpdate(Device device);

	int intoWarehouse(Map<String, Object> dataMap);

}
