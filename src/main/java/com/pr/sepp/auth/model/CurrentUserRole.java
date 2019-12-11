package com.pr.sepp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserRole {
    private Integer productId;
    private String productCode;
    private String productName;
    private String isValid;
    private Integer roleId;
    private String roleName;
    private String roleCode;
}
