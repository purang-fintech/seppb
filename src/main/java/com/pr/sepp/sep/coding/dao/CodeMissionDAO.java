package com.pr.sepp.sep.coding.dao;

import com.pr.sepp.sep.coding.model.CodeMission;

import java.util.List;
import java.util.Map;

public interface CodeMissionDAO {

    List<CodeMission> cmsQuery(Map<String, Object> dataMap);

    int cmsCreate(CodeMission codeMission);

    int cmsUpdate(CodeMission codeMission);

    int cmsStatusUpdate(int status, int id);

	int reqCmsStatusSync(Integer reqId, Integer status);
}
