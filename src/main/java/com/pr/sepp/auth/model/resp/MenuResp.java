package com.pr.sepp.auth.model.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResp {
    private Integer id;
    private String icon;
    private String index;
    private String title;
    private String label;
    private List<Integer> roleIds;
    private List<Integer> shows;
    private List<MenuResp> subs;
    private List<MenuResp> children;

}
