package com.pr.sepp.sep.build.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstanceEnv {
    private Integer id;
    private Integer productId;
    private Integer envType;
    private Integer branchId;
    private String instance;
    private String jobName;
    private String createdDate;

    public static Map<Integer, List<InstanceEnv>> groupingByEnvType(List<InstanceEnv> instanceEnvs) {
        return instanceEnvs.stream().collect(Collectors.groupingBy(InstanceEnv::getEnvType));
    }

}
