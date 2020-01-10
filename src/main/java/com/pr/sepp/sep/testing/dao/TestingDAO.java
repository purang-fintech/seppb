package com.pr.sepp.sep.testing.dao;

import com.pr.sepp.sep.testing.model.*;

import java.util.List;
import java.util.Map;

public interface TestingDAO {

	List<CaseFolder> treeQuery(Map<String, Object> dataMap);

	int caseFolderCreate(CaseFolder caseFolder);

	Integer caseFolderQuery(Map<String, Object> dataMap);

	int caseFolderDelete(Integer id);

	int caseFolderUpdate(CaseFolder caseFolder);

	List<Map<String, Object>> caseStepQuery(Integer caseId);

	int caseStepSave(CaseStep caseStep);

	int caseStepDelete(Integer caseId, Integer stepId);

	int caseStepsDelete(Integer caseId);

	int caseResultDelete(Integer caseId);

	int reduceStepIdOnDel(Integer caseId, Integer stepId);

	int caseStepUpdate(Map<String, Object> dataMap);

	List<CaseInfo> caseInfoQuery(Integer caseId);

	int caseInfoSave(CaseInfo caseInfo);

	int caseStatusUpdate(Integer caseId, Integer status);

	int caseInfoDelete(Integer caseId);

	List<Map<String, Object>> caseReadQuery(Integer caseId);

	List<Map<String, Object>> caseRelateInfoQuery(Integer caseId);

	int caseRelateSave(Map<String, Object> dataMap);

	int caseRelateDelete(CaseRelationShip caseRelationShip);

	List<Map<String, Object>> relatedDefectQuery(Integer caseId);

	int caseInfoPaste(Map<String, Object> dataMap);

	List<Map<String, Object>> relatedReqQuery(Integer caseId);

	int caseMindDelete(Integer caseId);

	int caseMindSave(CaseMind mind);

	int caseStepPaste(Integer caseId, Integer originId);

	int caseMindPaste(Integer caseId, Integer originId);

	String caseMindQuery(Integer caseId);
}
