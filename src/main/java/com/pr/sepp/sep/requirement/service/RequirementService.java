package com.pr.sepp.sep.requirement.service;

import com.pr.sepp.sep.requirement.model.ReqStatusUpdate;
import com.pr.sepp.sep.requirement.model.Requirement;

import java.util.List;
import java.util.Map;

public interface RequirementService {

	int reqCreate(Requirement requirement, Integer isProblem);

	int reqUpdate(Requirement requirement, int reqChanged) throws IllegalAccessException;

	int reqStatusUpdate(ReqStatusUpdate reqStatusUpdate);

	int reqRelease(String mappedJson);

	int reqUnRelease(List<String> reqs);

	List<Requirement> reqQuery(Map<String, Object> dataMap);

	List<Requirement> relReqQuery(Integer relId, Integer pageNum, Integer pageSize);

	List<Requirement> reqBatchQuery(Map<String, Object> dataMap);

	List<Map<String, String>> reqHistoryQuery(Integer reqId);

	int reqDelayCopy(Integer id);
}
