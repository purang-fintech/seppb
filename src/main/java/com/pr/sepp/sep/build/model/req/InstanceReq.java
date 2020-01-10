package com.pr.sepp.sep.build.model.req;


import com.pr.sepp.sep.build.model.BuildInstance;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import lombok.Data;

@Data
public class InstanceReq {

	private Integer id;
	private String instance;
	private String description;
	private String projectName;
	private InstanceType type;
	private String params;

	public BuildInstance copy() {
		BuildInstance buildInstance = new BuildInstance();
		buildInstance.setInstance(instance);
		buildInstance.setDescription(description);
		buildInstance.setProjectName(projectName);
		buildInstance.setType(type);
		buildInstance.setId(id);
		buildInstance.setParams(params);
		return buildInstance;
	}
}
