package com.pr.sepp.utils.sonar;

import com.pr.sepp.mgr.system.model.SystemSetting;
import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;

@Data
public class SonarProperties {

	@Data
	public static class SonarConfig {
		private String baseHost;
		private String login;
		private String password;
	}

	public static List<SonarConfig> settingToSonarConfig(SystemSetting systemSetting) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(systemSetting.getSettingValue(), new TypeReference<List<SonarConfig>>() {
		});
	}
}
