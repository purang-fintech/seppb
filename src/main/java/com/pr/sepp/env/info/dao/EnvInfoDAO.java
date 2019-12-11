package com.pr.sepp.env.info.dao;

import com.pr.sepp.env.info.model.EnvInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EnvInfoDAO {

	List<EnvInfo> envInfoQuery(Map<String, Object> dataMap);

	List<String> instanceQuery(Integer productId);

	int envInfoCreate(EnvInfo envInfo);

	int envInfoUpdate(EnvInfo envInfo);

	int envInfoDelete(Integer envInfoId);

	EnvInfo findEnvInfo(@Param("productId") Integer productId, @Param("branchId") Integer branchId,
						@Param("envType") Integer envType, @Param("instance") String instance);

	List<EnvInfo> selectAllJobEnvInfo();

	List<EnvInfo> findEnvInfos(@Param("productId") Integer productId, @Param("branchId") Integer branchId,
							   @Param("envType") Integer envType);
}
