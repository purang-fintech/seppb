package com.pr.sepp.reports.fuzz.dao;

import com.pr.sepp.sep.coding.model.CodeMission;
import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.sep.problem.model.Problem;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.requirement.model.Requirement;

import java.util.List;
import java.util.Map;

public interface FuzzQueryDAO {
	
	List<Release> releaseQuery(Map<String, String> dataMap);

	List<Requirement> reqQuery(Map<String, String> dataMap);

	List<CodeMission> cmsQuery(Map<String, String> dataMap);
	
	List<Defect> defectQuery(Map<String, String> dataMap);
	
	List<Problem> problemQuery(Map<String, String> dataMap);
	
}
