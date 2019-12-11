package com.pr.sepp.sep.build.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(exclude = {"relCode", "statusName", "submitterName", "reqSubmitterName", "proderName", "deverName", "testerName",
		"devResponser", "pdResponser", "testResponser", "sitDate", "uatDate"})
public class ReleaseNote {

	private Integer id;
	private Integer reqId;
	private Integer status;
	private Integer relId;
	private Integer submitter;
	private List<BuildFile> buildFiles;
	private String description;
	private String files;
	private String attachment;
	private String others;
	private String reqSummary;
	private Integer reqSubmitter;
	private String expectDate;
	private String submitDate;
	private Integer pdResponser;
	private Integer devResponser;
	private Integer testResponser;

	private String sitDate;
	private String uatDate;
	private String relCode;
	private String statusName;
	private String submitterName;
	private String reqSubmitterName;
	private String proderName;
	private String deverName;
	private String testerName;
}