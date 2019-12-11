package com.pr.sepp.sep.device.model;

import lombok.Data;

@Data
public class Device {
	private Integer id;
	private String assetId;
	private String deviceName;
	private String brand;
	private String oprSys;
	private String model;
	private String color;
	private String versions;
	private Integer ram;
	private Integer rom;
	private String status;
	private String userName;
	private String rentDate;
	private String returnDate;
	private Integer controller;
	private String controllerName;
}
