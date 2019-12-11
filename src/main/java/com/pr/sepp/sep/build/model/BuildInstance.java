package com.pr.sepp.sep.build.model;

import com.pr.sepp.sep.build.model.constants.InstanceType;
import lombok.Data;

@Data
public class BuildInstance {
    private Integer id;
    private String instance;
    private Integer productId;
    private String user;
    private InstanceType type;
    private String description;
    private String params;

    private String createdDate;
    private String updatedDate;
}
