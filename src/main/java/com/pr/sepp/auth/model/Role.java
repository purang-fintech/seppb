package com.pr.sepp.auth.model;

import lombok.Data;

@Data
public class Role {
    private Integer roleId;
    private String roleCode;
    private String roleName;
    private String isValid;
    private String roleDesc;
    private String updatedDate;
}
