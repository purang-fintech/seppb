package com.pr.sepp.sep.change.service.impl;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.sep.change.dao.ChangeDAO;
import com.pr.sepp.sep.change.model.Change;
import com.pr.sepp.sep.change.service.ChangeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service("changeService")
public class ChangeServiceImpl implements ChangeService {

	@Autowired
	private ChangeDAO changeDAO;

	@Override
	public List<Change> changeQuery(int reqId) {
		return changeDAO.changeQuery(reqId);
	}

	@Override
	public List<Change> changeAuditQuery(Map<String, Object> dataMap) {
		return changeDAO.changeAuditQuery(dataMap);
	}

	@Override
	public int changeInfoCreate(Change change) {
		return changeDAO.changeInfoCreate(change);
	}

	@Override
	public int changeInfoUpdate(int id) {
		Map<String, Object> dataMap = new HashMap<>();
		int userId = ParameterThreadLocal.getUserId();
		dataMap.put(CommonParameter.ID, id);
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<Change> changes = changeDAO.changeAuditQuery(dataMap);
		if (changes.size() == 0) {
			return 0;
		}
		Change change = changes.get(0);

		String auditted = change.getAudittedUser();
		if (("," + change.getAudittedUser() + ",").indexOf("," + userId + ",") > -1) {
			auditted = change.getAudittedUser();
		} else {
			auditted = (StringUtils.isEmpty(auditted)) ? userId + "" : auditted + "," + userId;
		}
		int status = 1;
		if (StringUtils.isEmpty(change.getAuditUser())) {
			status = 0;
		} else {
			String[] adted = auditted.split(",");
			Arrays.sort(adted);
			String[] toadt = change.getAuditUser().split(",");
			Arrays.sort(toadt);
			String adtedStr = "";
			String toadtStr = "";
			for (int i = 0; i < adted.length; i++) {
				adtedStr += adted[i];
			}
			for (int i = 0; i < toadt.length; i++) {
				toadtStr += toadt[i];
			}
			if (adtedStr.equals(toadtStr)) {
				status = 0;
			}
		}

		change.setAudittedUser(auditted);
		change.setChangeStatus(status);

		return changeDAO.changeInfoUpdate(change);
	}

	@Override
	public String changeAuditorQuery(int productId, int reqId) {
		List<String> auditors = new ArrayList<String>();
		String auditorRoles = changeDAO.changeAuditorRolesQuery(productId);
		if (StringUtils.isEmpty(auditorRoles)) {
			return null;
		}
		String[] roles = auditorRoles.split(",");
		for (int i = 0; i < roles.length; i++) {
			if (Integer.parseInt(roles[i]) == 1) {
				String user = changeDAO.changeDeverQuery(reqId);
				if (!StringUtils.isEmpty(user)) {
					auditors.add(user);
				}
			}
			if (Integer.parseInt(roles[i]) == 2) {
				String user = changeDAO.changeTesterQuery(reqId);
				if (!StringUtils.isEmpty(user)) {
					auditors.add(user);
				}
			}
			if (Integer.parseInt(roles[i]) == 3) {
				String user = changeDAO.projectManagerQuery(productId);
				if (!StringUtils.isEmpty(user)) {
					auditors.add(user);
				}
			}
			if (Integer.parseInt(roles[i]) == 4) {
				String user = changeDAO.productDirectorQuery(reqId);
				if (!StringUtils.isEmpty(user)) {
					auditors.add(user);
				}
			}
			if (Integer.parseInt(roles[i]) == 5) {
				String user = changeDAO.changeDevMgrQuery(reqId);
				if (!StringUtils.isEmpty(user)) {
					auditors.add(user);
				}
			}
			if (Integer.parseInt(roles[i]) == 6) {
				String user = changeDAO.changeTestMgrQuery(reqId);
				if (!StringUtils.isEmpty(user)) {
					auditors.add(user);
				}
			}
		}
		return auditors.size() == 0 ? null : listDistinctToString(auditors);
	}

	private String listDistinctToString(List<String> auditors) {
		Set<String> set = new HashSet<String>(Arrays.asList(auditors.toString().replace("[", "").replace("]", "").replaceAll(" ", "").split(",")));
		String userStr = "";
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			userStr += iterator.next() + ",";
		}
		userStr = userStr.substring(0, userStr.length() - 1);
		return userStr;
	}

	@Override
	public int changeOnWay(Map<String, Object> dataMap) {
		return changeDAO.changeOnWay(dataMap);
	}

	@Override
	public String changeAuditorRolesQuery(int productId) {
		return changeDAO.changeAuditorRolesQuery(productId);
	}
}
