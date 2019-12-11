package com.pr.sepp.sep.testing.service;

import com.pr.sepp.sep.testing.model.*;

import java.util.List;
import java.util.Map;

public interface TestingService {

	List<CaseFolder> treeQuery(Map<String, Object> dataMap);

	int caseFolderCreate(CaseFolder caseFolder);

	int caseFolderDelete(Integer id);

	int caseFolderUpdate(CaseFolder caseFolder);

	Map<String, Object> caseUpload(String filePath);

	List<Map<String, Object>> caseStepQuery(Integer caseId);

	int caseStepSave(CaseStep caseStep);

	int caseMindSave(CaseMind caseMind);

	int caseStepDelete(Integer caseId, Integer stepId);

	List<CaseInfo> caseInfoQuery(Integer caseId);

	int caseInfoSave(CaseInfo caseInfo) throws IllegalAccessException;

	int caseInfoDelete(Integer caseId);

	int caseStepUpdate(Map<String, Object> dataMap);

	Map<String, Object> caseReadOnlyQuery(Integer caseId);

	int caseRelateSave(CaseRelationShip caseRelationShip);

	int caseRelateDelete(CaseRelationShip caseRelationShip);

	List<Map<String, Object>> relatedDefectQuery(Integer caseId);

	int caseFolderPaste(CaseFolder caseFolder);

	List<Map<String, Object>> relatedReqQuery(Integer caseId);

	String caseMindQuery(Integer caseId);
}
