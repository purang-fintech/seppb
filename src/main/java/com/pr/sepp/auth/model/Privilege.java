package com.pr.sepp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Privilege {
    private Integer privId;
    private Integer userId;
    private Integer roleId;
    private Integer productId;

}
