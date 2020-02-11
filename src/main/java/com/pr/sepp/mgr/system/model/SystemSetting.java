package com.pr.sepp.mgr.system.model;

import com.pr.sepp.sep.build.model.constants.InstanceType;
import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;

@Data
public class SystemSetting {
    private Integer id;
    private Integer configId;
    private Integer createUser;
    private String settingValue;

    @Data
    public static class JenkinsConfig {
        private String builderServer;
        private String builderUser;
        private String password;
        private InstanceType serverType;
        private boolean disabled;
    }

    public static List<JenkinsConfig> settingToJenkinsConfig(SystemSetting systemSetting) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(systemSetting.getSettingValue(), new TypeReference<List<JenkinsConfig>>() {
        });
    }
}