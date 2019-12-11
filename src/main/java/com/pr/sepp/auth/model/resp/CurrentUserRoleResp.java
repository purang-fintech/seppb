package com.pr.sepp.auth.model.resp;

import com.pr.sepp.auth.model.CurrentUserRole;
import com.pr.sepp.mgr.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserRoleResp {

    private List<String> roleNames;
    private List<Integer> roleIds;
    private List<String> roleCodes;

    private Map<Integer, List<Integer>> productRoleIds;
    private Map<Integer, List<String>> productRoleCodes;

    private List<Product> products;


    public static CurrentUserRoleResp buildSimpleResp(List<CurrentUserRole> currentUserRoles) {
        CurrentUserRoleResp currentUserRoleResp = new CurrentUserRoleResp();
        List<String> roleCodes = currentUserRoles.stream().map(CurrentUserRole::getRoleCode).collect(Collectors.toList());
        List<Integer> roleIds = currentUserRoles.stream().map(CurrentUserRole::getRoleId).collect(Collectors.toList());
        currentUserRoleResp.setRoleIds(roleIds);
        currentUserRoleResp.setRoleCodes(roleCodes);
        return currentUserRoleResp;
    }
}
