package com.pr.sepp.env.info.service;

import java.util.List;
import java.util.Map;

import com.pr.sepp.env.info.model.EnvInfo;

public interface EnvInfoService {

	List<EnvInfo> envInfoQuery(Map<String, Object> dataMap);

	List<String> instanceQuery(Integer productId);

	int envInfoCreate(EnvInfo envInfo);

	int envInfoUpdate(EnvInfo envInfo) throws IllegalAccessException;

	int envInfoDelete(Integer envInfoId);

	Map<String,String> getJobNames(Integer envType,Integer branchId);

}
