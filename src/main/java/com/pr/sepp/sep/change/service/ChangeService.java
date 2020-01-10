package com.pr.sepp.sep.change.service;

import com.pr.sepp.sep.change.model.Change;

import java.util.List;
import java.util.Map;

public interface ChangeService {

	List<Change> changeQuery(int reqId);

	List<Change> changeAuditQuery(Map<String, Object> dataMap);

	int changeInfoCreate(Change change);

	int changeInfoUpdate(int id);

	String changeAuditorQuery(int productId, int reqId);

	String changeAuditorRolesQuery(int productId);

	int changeOnWay(Map<String, Object> dataMap);

}
