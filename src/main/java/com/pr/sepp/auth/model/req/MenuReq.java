package com.pr.sepp.auth.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuReq {


    private Integer id;
    private String menuIcon;
    private String menuIndex;
    @NotEmpty(message = "菜单名不能为空")
    private String title;
    private List<Integer> roleIds;
    private Integer parentId;
}
