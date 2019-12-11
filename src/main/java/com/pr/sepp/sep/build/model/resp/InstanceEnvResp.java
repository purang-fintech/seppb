package com.pr.sepp.sep.build.model.resp;


import com.pr.sepp.sep.build.model.InstanceEnv;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Data
public class InstanceEnvResp {
    private String envName;
    private Integer envType;
    private List<InstanceEnv> instanceEnvs = Lists.newArrayList();


    public void setInstanceEnvs(List<InstanceEnv> instanceEnvs) {
        if (isNotEmpty(instanceEnvs)) {
            this.instanceEnvs = instanceEnvs;
        }
    }
}
