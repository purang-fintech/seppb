package com.pr.sepp.file.service;

import com.pr.sepp.file.model.SEPPFile;
import com.pr.sepp.file.model.TestMail;

import java.util.List;
import java.util.Map;

public interface FileService {

	int attachCreate(SEPPFile file);

	int attachDelete(Map<String, String> dataMap);

	List<SEPPFile> attachQuery(Map<String, Object> dataMap);

	void sendAttachMail(TestMail testMail);

	byte[] fileDownload(String fileObject, String groupName);
}
