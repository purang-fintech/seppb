package com.pr.sepp.sep.build.model.req;


import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import lombok.Data;

@Data
public class InstanceReq {

	private Integer id;
	private String instance;
	private String description;
	private String repoUrl;
	private String projectName;
	private String namespace;
	private InstanceType type;
	private String params;

	public BuildInstance copy() {
		BuildInstance buildInstance = new BuildInstance();
		buildInstance.setInstance(instance);
		buildInstance.setDescription(description);
		buildInstance.setRepoUrl(repoUrl);
		buildInstance.setProjectName(projectName);
		buildInstance.setNamespace(namespace);
		buildInstance.setType(type);
		buildInstance.setId(id);
		buildInstance.setParams(params);
		return buildInstance;
	}
}
