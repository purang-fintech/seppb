package com.pr.sepp.sep.requirement.dao;

import com.pr.sepp.sep.requirement.model.ReqStatusUpdate;
import com.pr.sepp.sep.requirement.model.Requirement;

import java.util.List;
import java.util.Map;

public interface RequirementDAO {

    int reqCreate(Requirement dataMap);

    int reqUpdate(Requirement dataMap);

    int reqStatusUpdate(ReqStatusUpdate reqStatusUpdate);

    List<Requirement> reqQuery(Map<String, Object> dataMap);

    List<Requirement> reqBatchQuery(Map<String, Object> dataMap);

    List<Map<String, String>> reqHistoryQuery(Integer reqId);

	int reqRelease(Map<String, Object> dataMap);

	int reqUnRelease(Map<String, Object> dataMap);

	int reqDefectRelease(Map<String, Object> dataMap);

	int reqDefectUnRelease(Map<String, Object> dataMap);

	List<Requirement> relReqQuery(Integer relId);

	int moveReqDefectNotClosed(Integer newReqId, int oldReqId);

	int moveReqCmsNotCompleted(Integer newReqId, int oldReqId);
}
