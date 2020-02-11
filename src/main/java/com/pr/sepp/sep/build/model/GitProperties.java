package com.pr.sepp.sep.build.model;

import com.pr.sepp.mgr.system.model.SystemSetting;
import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;

@Data
public class GitProperties {
    @Data
    public static class GitConfig {
        private String repoUrl;
        private String apiToken;

    }

    public static List<GitProperties.GitConfig> settingToGitConfig(SystemSetting systemSetting) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(systemSetting.getSettingValue(), new TypeReference<List<GitProperties.GitConfig>>() {
        });
    }
}
