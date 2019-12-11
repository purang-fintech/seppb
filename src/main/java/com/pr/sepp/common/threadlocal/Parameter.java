package com.pr.sepp.common.threadlocal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Parameter {
    private Integer userId;
    private String userName;
    private String Name;
    private Integer productId;
    private String email;
    private String httpId;
    private Integer pageNum;
    private Integer pageSize;

}
