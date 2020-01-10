package com.pr.sepp.base.service.impl;

import com.pr.sepp.base.dao.BaseQueryDAO;
import com.pr.sepp.base.model.*;
import com.pr.sepp.base.service.BaseQueryService;
import com.pr.sepp.mgr.product.model.ProductBranch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BaseQueryServiceImpl implements BaseQueryService {

    @Autowired
    BaseQueryDAO baseQueryDAO;

    @Override
    public Map<String, Object> baseQueryProduct(int productId) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("product", baseQueryDAO.product());
        dataMap.put("autotestType", baseQueryDAO.autotestType());
        dataMap.put("buildStatus", baseQueryDAO.buildStatus());
        dataMap.put("releaseNoteStatus", baseQueryDAO.releaseNoteStatus());
        dataMap.put("caseRelateType", baseQueryDAO.caseRelateType());
        dataMap.put("codeMissionStatus", baseQueryDAO.codeMissionStatus());
        dataMap.put("dataUnit", baseQueryDAO.dataUnit());
        dataMap.put("warningType", baseQueryDAO.warningType());
        dataMap.put("warningLevel", baseQueryDAO.warningLevel());
        dataMap.put("userRoles", baseQueryDAO.userRoles());
        dataMap.put("testType", baseQueryDAO.testType());
        dataMap.put("testStatus", baseQueryDAO.testStatus());
        dataMap.put("testResultStatus", baseQueryDAO.testResultStatus());
        dataMap.put("testPriority", baseQueryDAO.testPriority());
        dataMap.put("testPeriod", baseQueryDAO.testPeriod());
        dataMap.put("testMissionStatus", baseQueryDAO.testMissionStatus());
        dataMap.put("testMeans", baseQueryDAO.testMeans());
        dataMap.put("requirementType", baseQueryDAO.requirementType());
        dataMap.put("requirementClose", baseQueryDAO.requirementClose());
        dataMap.put("requirementStatus", baseQueryDAO.requirementStatus());
        dataMap.put("prStatus", baseQueryDAO.prStatus());
        dataMap.put("requirementPriority", baseQueryDAO.requirementPriority());
        dataMap.put("reportType", baseQueryDAO.reportType());
        dataMap.put("releaseStatus", baseQueryDAO.releaseStatus());
        dataMap.put("productBranch", baseQueryDAO.productBranch(productId));
        dataMap.put("problemType", baseQueryDAO.problemType());
        dataMap.put("problemStatus", baseQueryDAO.problemStatus());
        dataMap.put("problemResolve", baseQueryDAO.problemResolve());
        dataMap.put("problemImprove", baseQueryDAO.problemImprove());
        dataMap.put("objectType", baseQueryDAO.objectType());
        dataMap.put("messageGateway", baseQueryDAO.messageGateway());
        dataMap.put("environmentType", baseQueryDAO.environmentType());
        dataMap.put("defectType", baseQueryDAO.defectType());
        dataMap.put("defectStatus", baseQueryDAO.defectStatus());
        dataMap.put("defectRefuseReason", baseQueryDAO.defectRefuseReason());
        dataMap.put("defectPriority", baseQueryDAO.defectPriority());
        dataMap.put("defectPeriod", baseQueryDAO.defectPeriod());
        dataMap.put("defectInfluence", baseQueryDAO.defectInfluence());
        dataMap.put("defectFoundPeriod", baseQueryDAO.defectFoundPeriod());
        dataMap.put("defectFoundMeans", baseQueryDAO.defectFoundMeans());
        return dataMap;
    }

    @Override
    public Map<String, Object> baseQueryNonParams() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("product", baseQueryDAO.product());
        dataMap.put("autotestType", baseQueryDAO.autotestType());
        dataMap.put("buildStatus", baseQueryDAO.buildStatus());
        dataMap.put("releaseNoteStatus", baseQueryDAO.releaseNoteStatus());
        dataMap.put("caseRelateType", baseQueryDAO.caseRelateType());
        dataMap.put("codeMissionStatus", baseQueryDAO.codeMissionStatus());
        dataMap.put("dataUnit", baseQueryDAO.dataUnit());
        dataMap.put("warningType", baseQueryDAO.warningType());
        dataMap.put("warningLevel", baseQueryDAO.warningLevel());
        dataMap.put("userRoles", baseQueryDAO.userRoles());
        dataMap.put("testType", baseQueryDAO.testType());
        dataMap.put("testStatus", baseQueryDAO.testStatus());
        dataMap.put("testResultStatus", baseQueryDAO.testResultStatus());
        dataMap.put("testPriority", baseQueryDAO.testPriority());
        dataMap.put("testPeriod", baseQueryDAO.testPeriod());
        dataMap.put("testMissionStatus", baseQueryDAO.testMissionStatus());
        dataMap.put("testMeans", baseQueryDAO.testMeans());
        dataMap.put("prStatus", baseQueryDAO.prStatus());
        dataMap.put("requirementType", baseQueryDAO.requirementType());
        dataMap.put("requirementClose", baseQueryDAO.requirementClose());
        dataMap.put("requirementStatus", baseQueryDAO.requirementStatus());
        dataMap.put("requirementPriority", baseQueryDAO.requirementPriority());
        dataMap.put("reportType", baseQueryDAO.reportType());
        dataMap.put("releaseStatus", baseQueryDAO.releaseStatus());
        dataMap.put("problemType", baseQueryDAO.problemType());
        dataMap.put("problemStatus", baseQueryDAO.problemStatus());
        dataMap.put("problemResolve", baseQueryDAO.problemResolve());
        dataMap.put("problemImprove", baseQueryDAO.problemImprove());
        dataMap.put("objectType", baseQueryDAO.objectType());
        dataMap.put("messageGateway", baseQueryDAO.messageGateway());
        dataMap.put("environmentType", baseQueryDAO.environmentType());
        dataMap.put("defectType", baseQueryDAO.defectType());
        dataMap.put("defectRefuseReason", baseQueryDAO.defectRefuseReason());
        dataMap.put("defectStatus", baseQueryDAO.defectStatus());
        dataMap.put("defectPriority", baseQueryDAO.defectPriority());
        dataMap.put("defectPeriod", baseQueryDAO.defectPeriod());
        dataMap.put("defectInfluence", baseQueryDAO.defectInfluence());
        dataMap.put("defectFoundPeriod", baseQueryDAO.defectFoundPeriod());
        dataMap.put("defectFoundMeans", baseQueryDAO.defectFoundMeans());
        return dataMap;
    }

    @Override
    public List<AutotestType> autotestType() {
        return baseQueryDAO.autotestType();
    }

    @Override
    public List<ReleaseNoteStatus> releaseNoteStatus() {
        return baseQueryDAO.releaseNoteStatus();
    }

    @Override
    public List<BuildStatus> buildStatus() {
        return baseQueryDAO.buildStatus();
    }

    @Override
    public List<CaseRelateType> caseRelateType() {
        return baseQueryDAO.caseRelateType();
    }

    @Override
    public List<CodeMissionStatus> codeMissionStatus() {
        return baseQueryDAO.codeMissionStatus();
    }

    @Override
    public List<DataUnit> dataUnit() {
        return baseQueryDAO.dataUnit();
    }

    @Override
    public List<DefectFoundMeans> defectFoundMeans() {
        return baseQueryDAO.defectFoundMeans();
    }

    @Override
    public List<DefectFoundPeriod> defectFoundPeriod() {
        return baseQueryDAO.defectFoundPeriod();
    }

    @Override
    public List<DefectInfluence> defectInfluence() {
        return baseQueryDAO.defectInfluence();
    }

    @Override
    public List<DefectPeriod> defectPeriod() {
        return baseQueryDAO.defectPeriod();
    }

    @Override
    public List<DefectRefuseReason> defectRefuseReason() {
        return baseQueryDAO.defectRefuseReason();
    }

    @Override
    public List<DefectPriority> defectPriority() {
        return baseQueryDAO.defectPriority();
    }

    @Override
    public List<DefectStatus> defectStatus() {
        return baseQueryDAO.defectStatus();
    }

    @Override
    public List<DefectType> defectType() {
        return baseQueryDAO.defectType();
    }

    @Override
    public List<EnvironmentType> environmentType() {
        return baseQueryDAO.environmentType();
    }

    @Override
    public List<MessageGateway> messageGateway() {
        return baseQueryDAO.messageGateway();
    }

    @Override
    public List<ObjectType> objectType() {
        return baseQueryDAO.objectType();
    }

    @Override
    public List<ProblemImprove> problemImprove() {
        return baseQueryDAO.problemImprove();
    }

    @Override
    public List<ProblemResolve> problemResolve() {
        return baseQueryDAO.problemResolve();
    }

    @Override
    public List<ProblemStatus> problemStatus() {
        return baseQueryDAO.problemStatus();
    }

    @Override
    public List<ProblemType> problemType() {
        return baseQueryDAO.problemType();
    }

    @Override
    public List<ReleaseStatus> releaseStatus() {
        return baseQueryDAO.releaseStatus();
    }

    @Override
    public List<ProductBranch> productBranch(int productId) {
        return baseQueryDAO.productBranch(productId);
    }

    @Override
    public List<ReportType> reportType() {
        return baseQueryDAO.reportType();
    }

    @Override
    public List<PRStatus> prStatus() {
        return baseQueryDAO.prStatus();
    }

    @Override
    public List<RequirementPriority> requirementPriority() {
        return baseQueryDAO.requirementPriority();
    }

    @Override
    public List<RequirementStatus> requirementStatus() {
        return baseQueryDAO.requirementStatus();
    }

    @Override
    public List<RequirementClose> requirementClose() {
        return baseQueryDAO.requirementClose();
    }

    @Override
    public List<RequirementType> requirementType() {
        return baseQueryDAO.requirementType();
    }

    @Override
    public List<TestMeans> testMeans() {
        return baseQueryDAO.testMeans();
    }

    @Override
    public List<TestMissionStatus> testMissionStatus() {
        return baseQueryDAO.testMissionStatus();
    }

    @Override
    public List<TestPeriod> testPeriod() {
        return baseQueryDAO.testPeriod();
    }

    @Override
    public List<TestPriority> testPriority() {
        return baseQueryDAO.testPriority();
    }

    @Override
    public List<TestStatus> testStatus() {
        return baseQueryDAO.testStatus();
    }

    @Override
    public List<TestStatus> testResultStatus() {
        return baseQueryDAO.testResultStatus();
    }

    @Override
    public List<TestType> testType() {
        return baseQueryDAO.testType();
    }

    @Override
    public List<UserRoles> userRoles() {
        return baseQueryDAO.userRoles();
    }

    @Override
    public List<WarningLevel> warningLevel() {
        return baseQueryDAO.warningLevel();
    }

    @Override
    public List<WarningType> warningType() {
        return baseQueryDAO.warningType();
    }

    @Override
    public List<Product> product() {
        return baseQueryDAO.product();
    }
}