package com.pr.sepp.sep.build.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"resultName", "submitterName", "envTypeName"})
public class Build {
	private Integer id;
	private Integer noteId;
	private Integer productId;
	private String prodBranch;
	private Integer envType;
	private String instance;
	private String submitTime;
	private String buildStart;
	private String buildEnd;
	private Integer submitter;
	private String buildHost;
	private String buildResult;
	private String buildDetail;

	private String resultName;
	private String submitterName;
	private String envTypeName;
}
