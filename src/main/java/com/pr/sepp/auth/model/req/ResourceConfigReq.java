package com.pr.sepp.auth.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceConfigReq {
    private Integer id;
    private String resourceDesc;
    private String componentName;
    private String authId;
    private String requestUrl;
    private String requestMethod;
    private List<String> roles;
    private String user;
    private Boolean isValid;
    private String updatedDate;
    private String createdDate;
    //{"resourceDesc":"","componentName":"",
    // "authId":1,"requestUrl":"","requestMethod":"","roles":[],"isValid":true}

}
