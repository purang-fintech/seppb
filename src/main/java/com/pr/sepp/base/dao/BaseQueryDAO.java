package com.pr.sepp.base.dao;

import com.pr.sepp.base.model.*;
import com.pr.sepp.mgr.product.model.ProductBranch;

import java.util.List;

public interface BaseQueryDAO {
	List<AutotestType> autotestType();

	List<ReleaseNoteStatus> releaseNoteStatus();

	List<BuildStatus> buildStatus();

	List<CaseRelateType> caseRelateType();

	List<CodeMissionStatus> codeMissionStatus();

	List<DataUnit> dataUnit();

	List<DefectFoundMeans> defectFoundMeans();

	List<DefectFoundPeriod> defectFoundPeriod();

	List<DefectInfluence> defectInfluence();

	List<DefectRefuseReason> defectRefuseReason();

	List<DefectPeriod> defectPeriod();

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

	List<RequirementType> requirementType();

	List<RequirementClose> requirementClose();

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