package com.pr.sepp.reports.fuzz.service.impl;

import com.pr.sepp.reports.fuzz.dao.FuzzQueryDAO;
import com.pr.sepp.reports.fuzz.service.FuzzQueryService;
import com.pr.sepp.sep.coding.model.CodeMission;
import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.sep.problem.model.Problem;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.requirement.model.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Service("fuzzQueryService")
public class FuzzQueryServiceImpl implements FuzzQueryService {
	@Autowired
	private FuzzQueryDAO fuzzQueryDAO;

	@Override
	public List<Release> releaseQuery(Map<String, String> dataMap) {
		return fuzzQueryDAO.releaseQuery(dataMap);
	}

	@Override
	public List<Requirement> reqQuery(Map<String, String> dataMap) {
		return fuzzQueryDAO.reqQuery(dataMap);
	}

	@Override
	public List<CodeMission> cmsQuery(Map<String, String> dataMap) {
		return fuzzQueryDAO.cmsQuery(dataMap);
	}

	@Override
	public List<Defect> defectQuery(Map<String, String> dataMap) {
		return fuzzQueryDAO.defectQuery(dataMap);
	}

	@Override
	public List<Problem> problemQuery(Map<String, String> dataMap) {
		return fuzzQueryDAO.problemQuery(dataMap);
	}

}