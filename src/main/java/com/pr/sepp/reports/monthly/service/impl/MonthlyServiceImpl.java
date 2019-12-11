package com.pr.sepp.reports.monthly.service.impl;

import com.pr.sepp.reports.monthly.dao.MonthlyDAO;
import com.pr.sepp.reports.monthly.service.MonthlyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Service("monthlyService")
public class MonthlyServiceImpl implements MonthlyService {
    @Autowired
    private MonthlyDAO monthlyDAO;

    @Override
    public List<Map<String, Object>> monthDefectCount(Map<String, Object> dataMap) {
        return monthlyDAO.monthDefectCount(dataMap);
    }

    @Override
    public List<Map<String, Object>> monthReqCount(Map<String, Object> dataMap) {
        return monthlyDAO.monthReqCount(dataMap);
    }

    @Override
    public List<Map<String, Object>> monthDefectCost(Map<String, Object> dataMap) {
        return monthlyDAO.monthDefectCost(dataMap);
    }

    @Override
    public List<Map<String, Object>> monthReqCost(Map<String, Object> dataMap) {
        return monthlyDAO.monthReqCost(dataMap);
    }

    @Override
    public List<Map<String, Object>> reqCostTrend(Map<String, Object> dataMap) {
        return monthlyDAO.reqCostTrend(dataMap);
    }

    @Override
    public List<Map<String, Object>> reqChangeTrend(Map<String, Object> dataMap) {
        return monthlyDAO.reqChangeTrend(dataMap);
    }

    @Override
    public List<Map<String, Object>> defectCostTrendM(Map<String, Object> dataMap) {
        return monthlyDAO.defectCostTrendM(dataMap);
    }

    @Override
    public List<Map<String, Object>> defectCostTrendR(Map<String, Object> dataMap) {
        return monthlyDAO.defectCostTrendR(dataMap);
    }
}