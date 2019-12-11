package com.pr.sepp.history.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pr.sepp.history.dao.HistoryDAO;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;

@Transactional
@Service("historyService")
public class HistoryServiceImpl implements HistoryService {
    @Autowired
    HistoryDAO historyDao;

    @Override
    public int historyInsert(SEPPHistory history) {
        return historyDao.historyInsert(history);
    }

    @Override
    public int historyInsertBatch(List<SEPPHistory> history) {
        return historyDao.historyInsertBatch(history);
    }

    @Override
    public List<SEPPHistory> historyDirectQuery(Map<String, String> dataMap) {
        return historyDao.historyDirectQuery(dataMap);
    }

    @Override
    public List<SEPPHistory> historyReferQuery(Map<String, String> dataMap) {
        return historyDao.historyReferQuery(dataMap);
    }
}
