package com.pr.sepp.auth.controller;

import com.pr.sepp.auth.core.permission.annotation.ApiPermission;
import com.pr.sepp.auth.model.ResourceConfig;
import com.pr.sepp.auth.model.req.ResourceConfigReq;
import com.pr.sepp.auth.model.req.ResourceQuery;
import com.pr.sepp.auth.model.resp.ComponentAuthResp;
import com.pr.sepp.auth.model.resp.ResourceConfigResp;
import com.pr.sepp.auth.service.ResourceConfigService;
import com.pr.sepp.base.model.Page;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RestController
public class ResourceConfigController {

    @Autowired
    private ResourceConfigService resourceConfigService;

    @ApiPermission
    @PostMapping(value = "/resources/configs")
    public void addResourceConfig(@RequestBody ResourceConfigReq resourceConfigReq) {
        ResourceConfig resourceConfig = ResourceConfig.builder().build();
        BeanUtils.copyProperties(resourceConfigReq, resourceConfig);
        resourceConfigService.save(resourceConfig);
    }

    @PostMapping(value = "/resources/configs/query")
    public Page<ResourceConfigResp> listResourceConfigs(@RequestBody ResourceQuery resourceQuery) {
        return resourceConfigService.listResourceConfigs(resourceQuery);
    }

    @ApiPermission
    @PostMapping(value = "/resources/configs::update")
    public void updateResourceConfig(@RequestBody ResourceConfigReq resourceConfigReq) {
        ResourceConfig resourceConfig = ResourceConfig.builder().build();
        BeanUtils.copyProperties(resourceConfigReq, resourceConfig);
        resourceConfigService.update(resourceConfig);
    }

    @ApiPermission
    @DeleteMapping(value = "/resources/configs")
    public void deleteResourceConfig(@RequestParam("componentName") String componentName, @RequestParam("authId") String authId) {
        resourceConfigService.deleteByComponent(componentName, authId);
    }

    @GetMapping(value = "/resource/configs/auth/{componentName}")
    public List<ComponentAuthResp> componentAuthResps(@PathVariable("componentName") String componentName) {
        List<ResourceConfig> resourceConfigs = resourceConfigService.findComponentAuthByComponent(componentName);
        Map<String, List<ResourceConfig>> resourceConfigMap = resourceConfigs.stream().collect(groupingBy(ResourceConfig::getAuthId));
        List<ComponentAuthResp> componentAuthResps = Lists.newArrayList();
        resourceConfigMap.forEach((key, value) -> componentAuthResps.add(ComponentAuthResp.builder()
                .roleIds(value.stream().map(ResourceConfig::getRoleId).collect(toList()))
                .authId(key)
                .componentName(componentName).build()));
        return componentAuthResps;
    }

}
