package com.pr.sepp.history.dao;

import java.util.List;
import java.util.Map;

import com.pr.sepp.history.model.SEPPHistory;

public interface HistoryDAO {
	int historyInsert(SEPPHistory history);

	int historyInsertBatch(List<SEPPHistory> history);

	List<SEPPHistory> historyDirectQuery(Map<String, String> dataMap);

	List<SEPPHistory> historyReferQuery(Map<String, String> dataMap);
}
