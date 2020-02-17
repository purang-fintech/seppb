package com.pr.sepp.base.service;

import com.pr.sepp.base.model.*;
import com.pr.sepp.mgr.product.model.ProductBranch;

import java.util.List;
import java.util.Map;

public interface BaseQueryService {
	Map<String, Object> baseQueryProduct(Integer productId);

	Map<String, Object> baseQueryNonParams();

	List<AutotestType> autotestType();

	List<ReleaseNoteStatus> releaseNoteStatus();

	List<BuildStatus> buildStatus();

	List<CaseRelateType> caseRelateType();

	List<CodeMissionStatus> codeMissionStatus();

	List<DataUnit> dataUnit();

	List<DefectFoundMeans> defectFoundMeans();

	List<DefectFoundPeriod> defectFoundPeriod();

	List<DefectInfluence> defectInfluence();

	List<DefectPeriod> defectPeriod();

	List<DefectRefuseReason> defectRefuseReason();

	List<DefectPriority> defectPriority();

	List<DefectStatus> defectStatus();

	List<DefectType> defectType();

	List<EnvironmentType> environmentType();

	List<MessageGateway> messageGateway();

	List<ObjectType> objectType();

	List<ProblemImprove> problemImprove();

	List<ProblemResolve> problemResolve();

	List<ProblemStatus> problemStatus();

	List<ProblemType> problemType();

	List<ReleaseStatus> releaseStatus();

	List<ProductBranch> productBranch(Integer productId);

	List<ReportType> reportType();

	List<RequirementPriority> requirementPriority();

	List<RequirementStatus> requirementStatus();

	List<RequirementClose> requirementClose();

	List<RequirementType> requirementType();

	List<TestMeans> testMeans();

	List<TestMissionStatus> testMissionStatus();

	List<TestPeriod> testPeriod();

	List<TestPriority> testPriority();

	List<TestStatus> testStatus();

	List<TestStatus> testResultStatus();

	List<TestType> testType();

	List<UserRoles> userRoles();

	List<WarningLevel> warningLevel();

	List<WarningType> warningType();

	List<Product> product();

	List<PRStatus> prStatus();
}