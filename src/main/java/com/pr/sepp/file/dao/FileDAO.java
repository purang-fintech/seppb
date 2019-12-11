package com.pr.sepp.file.dao;

import java.util.List;
import java.util.Map;

import com.pr.sepp.file.model.SEPPFile;

public interface FileDAO {

	int attachCreate(SEPPFile file);

	int attachDelete(Map<String, String> dataMap);

	List<SEPPFile> attachQuery(Map<String, Object> dataMap);
}
