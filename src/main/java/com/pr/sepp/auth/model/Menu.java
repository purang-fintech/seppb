package com.pr.sepp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    private Integer id;
    private String menuIcon;
    private String menuIndex;
    private String title;
    private List<Integer> roleIds;
    private String roles;
    private Integer parentId;

    public static List<Integer> stringToList(String roles) {
        if (StringUtils.isEmpty(roles)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        List<Integer> roleIds = null;
        try {
            roleIds = mapper.readValue(roles, new TypeReference<List<Integer>>() {
            });
        } catch (IOException e) {
            log.error("序列化失败{}", e);
        }
        return roleIds;
    }

    public static String listToString(List<Integer> roleIds) {
        ObjectMapper mapper = new ObjectMapper();
        if (null == roleIds || roleIds.size() == 0) {
            return null;
        }
        try {
            return mapper.writeValueAsString(roleIds);
        } catch (IOException e) {
            log.error("序列化失败{}", e);
        }
        return null;
    }
}
