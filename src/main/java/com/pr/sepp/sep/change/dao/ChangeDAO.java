package com.pr.sepp.sep.change.dao;

import com.pr.sepp.sep.change.model.Change;

import java.util.List;
import java.util.Map;

public interface ChangeDAO {

	List<Change> changeQuery(int reqId);

	List<Change> changeAuditQuery(Map<String, Object> dataMap);

	int changeInfoCreate(Change change);

	int changeInfoUpdate(Change change);

	int changeOnWay(Map<String, Object> dataMap);

	String changeAuditorRolesQuery(int productId);

	String changeDeverQuery(int reqId);

	String changeTesterQuery(int reqId);

	String changeDevMgrQuery(int reqId);

	String changeTestMgrQuery(int reqId);

	String productDirectorQuery(int reqId);

	String projectManagerQuery(int productId);

}
