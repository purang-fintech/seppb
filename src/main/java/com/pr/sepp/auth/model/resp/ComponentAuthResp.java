package com.pr.sepp.auth.model.resp;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentAuthResp {

    private String componentName;
    private List<Integer> roleIds;
    private String authId;

    private static List<Integer> copyRoleIds(String roleIds) {
        if (StringUtils.isNotBlank(roleIds)) {
            String[] ids = StringUtils.split(roleIds, ",");
            return Arrays.stream(ids).map(Integer::valueOf).collect(toList());
        } else {
            return Lists.newArrayList();
        }
    }
}
