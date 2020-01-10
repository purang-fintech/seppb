package com.pr.sepp.sep.build.model.req;

import com.google.common.collect.Lists;
import com.pr.sepp.sep.build.model.BuildFile;
import com.pr.sepp.sep.build.model.constants.BuildType;
import com.pr.sepp.sep.build.model.constants.JenkinsBuildStatus;
import lombok.Data;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Data
public class BuildHistoryReq {
	private Integer id;
	private String jobName;
	private Integer noteId;
	private Integer productId;
	private Integer branchId;
	private Integer envType;
	private String gitVersion;
	private String instance;
	private String envName;
	private Integer buildVersion;
	private Integer submitter;
	private String buildHost;
	private JenkinsBuildStatus buildStatus;
	private String buildParams;
	private String files;
	private String tag;
	private String sonarScan;
	private List<String> instances;
	private LocalDateTime createdDate;
	private BuildType buildType;
	private Map<String, Boolean> buildMethod;

	public static String listToBuildParams(List<BuildFile> buildFiles) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(buildFiles);
		} catch (IOException e) {
		}
		return null;
	}


	public static String mapToBuildParams(Map<String, String> paramsMap) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<BuildFile> buildFiles = Lists.newArrayList();
		if (nonNull(paramsMap)) {
			paramsMap.forEach((key, value) -> buildFiles.add(BuildFile.builder().paramKey(key).paramValue(value).build()));
			return mapper.writeValueAsString(buildFiles);
		}
		return null;
	}

	public void buildInitData() {
		this.setTag(UUID.randomUUID().toString());
		this.setCreatedDate(LocalDateTime.now());
//        this.setBuildParams(listToBuildParams(buildFiles));
	}
}
