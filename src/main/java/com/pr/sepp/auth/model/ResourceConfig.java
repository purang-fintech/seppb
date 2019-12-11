package com.pr.sepp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceConfig {

    private Integer id;
    private String resourceDesc;
    private String componentName;
    private String authId;
    private String requestUrl;
    private String requestMethod;
    private String roleIds;
    private List<String> roles;
    private Integer roleId;
    private String user;
    private Boolean isValid;
    private Integer productId;
    private String updatedDate;
    private String createdDate;


}
