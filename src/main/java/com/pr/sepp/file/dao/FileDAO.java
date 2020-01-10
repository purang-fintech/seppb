package com.pr.sepp.file.dao;

import com.pr.sepp.file.model.SEPPFile;

import java.util.List;
import java.util.Map;

public interface FileDAO {

	int attachCreate(SEPPFile file);

	int attachDelete(Map<String, String> dataMap);

	List<SEPPFile> attachQuery(Map<String, Object> dataMap);
}
