package com.pr.sepp.file.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "uploadUserName")
public class SEPPFile {

	private int id;
	private String url;
	private String fileName;
	private String uploadDate;
	private int uploadUser;
	private String uploadUserName;

}
