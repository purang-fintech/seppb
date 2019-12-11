package com.pr.sepp.sep.coding.service;

import com.pr.sepp.sep.coding.model.CodeMission;

import java.util.List;
import java.util.Map;

public interface CodeMissionService {

	int cmsCreate(CodeMission codeMission);

	int cmsUpdate(CodeMission codeMission) throws IllegalAccessException;

	List<CodeMission> cmsQuery(Map<String, Object> dataMap);

	int cmsStatusUpdate(int status, int cmId);

	int reqCmsStatusSync(Integer reqId, Integer status);
}
