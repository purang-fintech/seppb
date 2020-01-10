package com.pr.sepp.auth.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.auth.dao.ResourceConfigDAO;
import com.pr.sepp.auth.model.ResourceConfig;
import com.pr.sepp.auth.model.Role;
import com.pr.sepp.auth.model.req.ResourceQuery;
import com.pr.sepp.auth.model.resp.ResourceConfigResp;
import com.pr.sepp.base.model.Page;
import com.pr.sepp.common.exception.SeppClientException;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.user.dao.UserDAO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ResourceConfigService {

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserDAO userDAO;

    public void save(ResourceConfig resourceConfig) {
        resourceConfig.setProductId(ParameterThreadLocal.getProductId());
        resourceConfig.setUser(userDAO.findUserByUserId(ParameterThreadLocal.getUserId()).getUserName());
        //重复性校验
        repeatValid(resourceConfig);
        resourceConfigDAO.batchInsert(resourceConfig);
    }

    public Page<ResourceConfigResp> listResourceConfigs(ResourceQuery resourceQuery) {
        resourceQuery.setProductId(ParameterThreadLocal.getProductId());
        PageHelper.startPage(resourceQuery.getPageNum(), resourceQuery.getPageSize());
        List<ResourceConfig> resourceConfigs = resourceConfigDAO.listResourceConfigs(resourceQuery);
        PageInfo<ResourceConfig> pageInfo = new PageInfo<>(resourceConfigs);
        List<ResourceConfig> resourceConfigsFromDb = pageInfo.getList();
        List<Role> roles = userRoleService.allRoles("Y");
        List<ResourceConfigResp> resourceConfigResps = resourceConfigsFromDb.stream()
                .map(resourceConfig -> ResourceConfigResp.resourceConfigCopy(resourceConfig, roles))
                .collect(toList());
        Page<ResourceConfigResp> page = new Page<>();
        page.setList(resourceConfigResps);
        page.setTotal(pageInfo.getPages());
        return page;
    }

    public void deleteByComponent(String componentName, String authId) {
        resourceConfigDAO.deleteByComponent(componentName, authId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(ResourceConfig resourceConfig) {
        //先删除
        deleteByComponent(resourceConfig.getComponentName(), resourceConfig.getAuthId());
        //再添加
        save(resourceConfig);
    }

    public List<ResourceConfig> listResourceConfigsByApi(String requestURI, String method) {
        return resourceConfigDAO.findResourceConfigsByApi(requestURI, method);
    }

    private void repeatValid(ResourceConfig resourceConfig) {
        List<ResourceConfig> resourceConfigs = resourceConfigDAO.findResourceConfigsByComponent(resourceConfig.getComponentName(),
                resourceConfig.getAuthId());
        if (CollectionUtils.isNotEmpty(resourceConfigs)) {
            throw new SeppClientException(String.format("请勿重复添加组件名为%s，id为%s的数据",
                    resourceConfig.getComponentName(), resourceConfig.getAuthId()));
        }
    }

    public List<ResourceConfig> findComponentAuthByComponent(String componentName) {
        return resourceConfigDAO.findResourceConfigsByComponent(componentName, null);
    }
}
