package com.pr.sepp.work.dashboard.dao;

import com.pr.sepp.notify.warning.model.Warning;
import com.pr.sepp.sep.coding.model.CodeMission;
import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.requirement.model.Requirement;

import java.util.List;
import java.util.Map;

public interface UserWorkDAO {
	List<Release> productOpenReleaseQuery(Integer productId);

	List<CodeMission> productOpenCmsQuery(Integer productId);

	List<Defect> productOpenDefectQuery(Integer productId);

	List<Requirement> productOpenRequestQuery(Integer productId);

	List<Requirement> releaseRequestQuery(Integer relId);

	List<Map<String, Object>> releaseScenarioCaseNotRun(Integer relId);

	List<Map<String, Object>> releaseScenarioCaseFailed(Integer relId);

	List<Map<String, Object>> releaseScenarioCaseSkipped(Integer relId);

	Integer caseRequestRelateQuery(Map<String, Object> releteMap);

	List<Map<String, Object>> releaseAllCaseQuery(Integer relId);

	List<Warning> productWarningQuery(Integer productId, String warningDate);
}
