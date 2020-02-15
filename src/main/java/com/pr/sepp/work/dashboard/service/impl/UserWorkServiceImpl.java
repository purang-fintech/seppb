package com.pr.sepp.work.dashboard.service.impl;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.notify.warning.model.Warning;
import com.pr.sepp.sep.coding.model.CodeMission;
import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.requirement.model.Requirement;
import com.pr.sepp.work.dashboard.dao.UserWorkDAO;
import com.pr.sepp.work.dashboard.service.UserWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service("userWorkService")
public class UserWorkServiceImpl implements UserWorkService {

	@Autowired
	private UserWorkDAO userWorkDAO;

	@Override
	public Map<String, Object> productReleaseQuery() {
		Map<String, Object> result = new HashMap<>();
		Integer me = ParameterThreadLocal.getUserId();
		List<Release> releases = userWorkDAO.productOpenReleaseQuery(ParameterThreadLocal.getProductId());

		// “我”创建或负责的版本
		List<Release> ownReleases = releases.stream().filter(d -> Objects.equals(d.getCreator(), me)
				|| Objects.equals(d.getResponser(), me)).collect(Collectors.toList());

		// “我”负责的需求在此版本内的
		List<Release> myReleases = new ArrayList<>();
		releases.forEach(item -> {
			List<Requirement> requirements = userWorkDAO.releaseRequestQuery(item.getId());
			for (Requirement req : requirements) {
				if (Objects.equals(req.getSubmitter(), me) || Objects.equals(req.getDevResponser(), me)
						|| Objects.equals(req.getTestResponser(), me) || Objects.equals(req.getPdResponser(), me)) {
					myReleases.add(item);
					break;
				}
			}
		});

		// 合并去重
		List<Release> myAllRelease = Stream.of(ownReleases, myReleases).flatMap(Collection::stream).distinct().collect(Collectors.toList());

		result.put("allData", releases);
		result.put("count", releases.size());
		result.put("mineData", myAllRelease);
		result.put("mine", myAllRelease.size());
		return result;
	}

	@Override
	public Map<String, Object> productRequestQuery() {
		Map<String, Object> result = new HashMap<>();
		Integer me = ParameterThreadLocal.getUserId();
		List<Requirement> requirements = userWorkDAO.productOpenRequestQuery(ParameterThreadLocal.getProductId());
		List<Requirement> myRequests = requirements.stream().filter(d -> Objects.equals(d.getSubmitter(), me)
				|| Objects.equals(d.getDevResponser(), me) || Objects.equals(d.getTestResponser(), me)
				|| Objects.equals(d.getPdResponser(), me)).collect(Collectors.toList());
		result.put("allData", requirements);
		result.put("mineData", myRequests);
		result.put("mine", myRequests.size());
		return result;
	}

	@Override
	public Map<String, Object> productMissionQuery() {
		Map<String, Object> result = new HashMap<>();
		Integer me = ParameterThreadLocal.getUserId();
		List<CodeMission> codeMissions = userWorkDAO.productOpenCmsQuery(ParameterThreadLocal.getProductId());
		List<CodeMission> myCodemissions = codeMissions.stream().filter(d -> Objects.equals(d.getResponser(), me)
				|| Objects.equals(d.getSpliter(), me)).collect(Collectors.toList());
		result.put("allData", codeMissions);
		result.put("mineData", myCodemissions);
		result.put("count", codeMissions.size());
		result.put("mine", myCodemissions.size());
		return result;
	}

	@Override
	public Map<String, Object> productDefectQuery() {
		Map<String, Object> result = new HashMap<>();
		Integer me = ParameterThreadLocal.getUserId();
		List<Defect> defects = userWorkDAO.productOpenDefectQuery(ParameterThreadLocal.getProductId());
		List<Defect> myDefects = defects.stream().filter(d -> Objects.equals(d.getResponser(), me)
				|| Objects.equals(d.getConciliator(), me) || Objects.equals(d.getSubmitter(), me)).collect(Collectors.toList());
		result.put("allData", defects);
		result.put("mineData", myDefects);
		result.put("count", defects.size());
		result.put("mine", myDefects.size());
		return result;
	}

	@Override
	public Map<String, Object> productTestingQuery() {
		Map<String, Object> result = new HashMap<>();
		Integer me = ParameterThreadLocal.getUserId();
		List<Release> releases = userWorkDAO.productOpenReleaseQuery(ParameterThreadLocal.getProductId());
		List<Map<String, Object>> allCases = new ArrayList<>();
		List<Map<String, Object>> notPassed = new ArrayList<>();
		List<Requirement> myRequests = new ArrayList<>();
		releases.forEach(release -> {
			allCases.addAll(userWorkDAO.releaseAllCaseQuery(release.getId()));
			notPassed.addAll(userWorkDAO.releaseScenarioCaseNotRun(release.getId()));
			notPassed.addAll(userWorkDAO.releaseScenarioCaseFailed(release.getId()));
			notPassed.addAll(userWorkDAO.releaseScenarioCaseSkipped(release.getId()));
			List<Requirement> requirements = userWorkDAO.releaseRequestQuery(release.getId());
			myRequests.addAll(requirements.stream().filter(d -> Objects.equals(d.getSubmitter(), me)
					|| Objects.equals(d.getDevResponser(), me) || Objects.equals(d.getTestResponser(), me)
					|| Objects.equals(d.getPdResponser(), me)).collect(Collectors.toList()));
		});
		Map<String, Object> releteMap = new HashMap<>();
		releteMap.put("cases", notPassed);
		releteMap.put("reqs", myRequests);

		int myCaseCount = 0;
		if (notPassed.size() > 0 && myRequests.size() > 0) {
			myCaseCount = userWorkDAO.caseRequestRelateQuery(releteMap);
		}

		result.put("count", allCases.size());
		result.put("notPassed", notPassed.size());
		result.put("mine", myCaseCount);
		return result;
	}

	@Override
	public Map<String, Object> productWarningQuery() {
		Map<String, Object> result = new HashMap<>();
		List<Warning> warnings = userWorkDAO.productWarningQuery(ParameterThreadLocal.getProductId(), null);
		List<Warning> myWarnings = warnings.stream().filter(f -> Objects.equals(f.getResponser(), ParameterThreadLocal.getUserId())).collect(Collectors.toList());
		result.put("count", warnings.size());
		result.put("mine", myWarnings.size());
		result.put("allData", warnings);
		result.put("mineData", myWarnings);
		return result;
	}

	@Override
	public Map<String, Object> productPipelineQuery() {
		return null;
	}

	@Override
	public Map<String, Object> productCodeHealthyQuery() {
		return null;
	}

	@Override
	public Map<String, Object> productCodeSecurityQuery() {
		return null;
	}

	@Override
	public Map<String, Object> productAutotestQuery() {
		return null;
	}

	@Override
	public Map<String, Object> productCoverageQuery() {
		return null;
	}
}
