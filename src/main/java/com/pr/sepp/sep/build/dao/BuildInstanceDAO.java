package com.pr.sepp.sep.build.dao;

import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.InstanceEnv;
import com.pr.sepp.sep.build.model.req.InstanceEnvReq;

import java.util.List;

public interface BuildInstanceDAO {

	void save(BuildInstance buildInstance);

	List<BuildInstance> listBuildInstances(Integer productId);


	BuildInstance findInstance(String instance, Integer productId);

	void deleteInstance(String instance);

	void update(BuildInstance buildInstance);

	void saveEnv(InstanceEnvReq instanceEnvReq);

	List<InstanceEnv> listInstanceEnvs(String instance, Integer productId);

	void updateEnv(InstanceEnvReq instanceEnvReq);

	InstanceEnv findEnv(Integer branchId, Integer envType, Integer productId, String instance);

	void deleteEnv(Integer id);


}
