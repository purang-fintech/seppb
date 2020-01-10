package com.pr.sepp.sep.device.service.impl;

import com.pr.sepp.sep.device.dao.DeviceDAO;
import com.pr.sepp.sep.device.model.Device;
import com.pr.sepp.sep.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DeviceServiceImpl implements DeviceService {
	@Autowired
	private DeviceDAO deviceDAO;

	@Override
	public List<Device> deviceQuery(Map<String, String> dataMap) {
		List<Device> result = deviceDAO.deviceQuery(dataMap);
		return result;
	}

	@Override
	public int changeUser(Map<String, Object> dataMap) {
		return deviceDAO.changeUser(dataMap);

	}

	@Override
	public int intoWarehouse(Map<String, Object> dataMap) {
		return deviceDAO.intoWarehouse(dataMap);
	}

	@Override
	public int deviceUpdate(Device device) {
		return deviceDAO.deviceUpdate(device);
	}

	@Override
	public int deviceCreate(Device device) {

		return deviceDAO.deviceCreate(device);
	}

}
