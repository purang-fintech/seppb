package com.pr.sepp.auth.model.resp;

import com.pr.sepp.auth.model.ResourceConfig;
import com.pr.sepp.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.join;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceConfigResp {
    private String resourceDesc;
    private String componentName;
    private String authId;
    private String requestUrl;
    private String requestMethod;
    private String roles;
    private List<Integer> roleIds;
    private String user;
    private Boolean isValid;
    private String updatedDate;
    private String createdDate;

    public static ResourceConfigResp resourceConfigCopy(ResourceConfig resourceConfig, List<Role> roles) {
        ResourceConfigResp resourceConfigResp = new ResourceConfigResp();
        String roleIds = resourceConfig.getRoleIds();
        BeanUtils.copyProperties(resourceConfig, resourceConfigResp);
        String[] ids = roleIds.split(",");
        List<String> roleNames = roleIdToRoleNames(roles, ids);
        resourceConfigResp.setRoles(join(roleNames, "、"));
        buildRoleIds(resourceConfigResp, ids);
        return resourceConfigResp;
    }

    private static List<String> roleIdToRoleNames(List<Role> roles, String[] ids) {
        Map<String, String> roleMap = roles.stream().collect(Collectors.toMap(role -> valueOf(role.getRoleId()), Role::getRoleName));
        return Arrays.stream(ids).map(roleMap::get).collect(toList());
    }

    private static void buildRoleIds(ResourceConfigResp resourceConfigResp, String[] ids) {
        resourceConfigResp.setRoleIds(Arrays.stream(ids).map(Integer::valueOf).collect(toList()));
    }
}
