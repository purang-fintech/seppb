package com.pr.sepp.auth.dao;

import com.pr.sepp.auth.model.ResourceConfig;
import com.pr.sepp.auth.model.req.ResourceQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourceConfigDAO {

    void batchInsert(@Param("resourceConfig") ResourceConfig resourceConfig);

    List<ResourceConfig> listResourceConfigs(ResourceQuery resourceQuery);

    List<ResourceConfig> findResourceConfigsByApi(String requestUrl, String requestMethod);

    List<ResourceConfig> findResourceConfigsByComponent(String componentName, String authId);

    void deleteByComponent(String componentName, String authId);
}
