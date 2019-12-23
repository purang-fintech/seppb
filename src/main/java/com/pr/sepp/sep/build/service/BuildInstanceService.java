package com.pr.sepp.sep.build.service;

import com.pr.sepp.base.dao.BaseQueryDAO;
import com.pr.sepp.base.model.EnvironmentType;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.sep.build.dao.BuildInstanceDAO;
import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.InstanceEnv;
import com.pr.sepp.sep.build.model.req.InstanceEnvReq;
import com.pr.sepp.sep.build.model.resp.InstanceEnvResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pr.sepp.sep.build.model.InstanceEnv.groupingByEnvType;
import static java.util.Objects.nonNull;

@Service
public class BuildInstanceService {

	@Autowired
	private BuildInstanceDAO buildInstanceDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private BaseQueryDAO baseQueryDAODAO;


	public void createInstance(BuildInstance buildInstance) {
		buildInstance.setProductId(ParameterThreadLocal.getProductId());
		buildInstance.setUser(userDAO.findUserByUserId(ParameterThreadLocal.getUserId()).getUserName());
		buildInstanceDAO.save(buildInstance);
	}

	public List<BuildInstance> listBuildInstances(Integer productId) {
		return buildInstanceDAO.listBuildInstances(productId);
	}

	public boolean isInstanceRepeat(String instance) {
		BuildInstance buildInstance = buildInstanceDAO.findInstance(instance, ParameterThreadLocal.getProductId());
		return nonNull(buildInstance);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteInstance(String instance) {
		buildInstanceDAO.deleteInstance(instance);
	}

	public void updateInstance(BuildInstance buildInstance) {
		buildInstance.setProductId(ParameterThreadLocal.getProductId());
		buildInstance.setUser(userDAO.findUserByUserId(ParameterThreadLocal.getUserId()).getUserName());
		buildInstanceDAO.update(buildInstance);
	}

	public void saveOrUpdateInstanceEnv(InstanceEnvReq instanceEnvReq) {
		if (Objects.nonNull(instanceEnvReq.getId())) {
			buildInstanceDAO.updateEnv(instanceEnvReq);
			return;
		}
		buildInstanceDAO.saveEnv(instanceEnvReq);
	}

	public List<InstanceEnvResp> listInstanceEnvs(String instance, Integer productId) {
		List<InstanceEnv> instanceEnvs = buildInstanceDAO.listInstanceEnvs(instance, productId);
		List<EnvironmentType> environmentTypes = baseQueryDAODAO.environmentType();
		return environmentTypes.stream().map(environmentType -> {
			Map<Integer, List<InstanceEnv>> instanceEnvMap = groupingByEnvType(instanceEnvs);
			InstanceEnvResp instanceEnvResp = new InstanceEnvResp();
			instanceEnvResp.setEnvName(environmentType.getTypeName());
			instanceEnvResp.setEnvType(environmentType.getTypeId());
			instanceEnvResp.setInstanceEnvs(instanceEnvMap.get(environmentType.getTypeId()));
			return instanceEnvResp;
		}).collect(Collectors.toList());
	}

	public boolean checkBranchRepeat(Integer branchId, Integer envType, Integer productId, String instance) {
		InstanceEnv instanceEnv = buildInstanceDAO.findEnv(branchId, envType, productId, instance);
		return Objects.nonNull(instanceEnv);
	}

	public void deleteEnv(Integer id) {
		buildInstanceDAO.deleteEnv(id);
	}
}
