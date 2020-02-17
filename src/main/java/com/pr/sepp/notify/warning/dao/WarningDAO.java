package com.pr.sepp.notify.warning.dao;

import com.pr.sepp.common.calculation.model.ReleaseSepData;
import com.pr.sepp.common.calculation.model.WarningRules;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.notify.warning.model.*;
import com.pr.sepp.sep.release.model.Release;

import java.util.List;

public interface WarningDAO {

	ReleaseSepData groupReleaseData(Integer relId, String warnDate);

	List<Product> warningProducts();

	List<WarningRules> warningRules();

	List<Release> productOpenRelease(Integer productId);

	int createBatch(WarningBatch warningBatch);

	List<WarningBatch> queryBatch(long batchNo);

	Integer createWarning(Warning warning);

	void createWarningBatch(List<Warning> warnings);

	void createWarningNotifyBatch(List<WarningNotify> warningNotifies);

	List<WarningMessage> warningQuery(WarningQuery warningQuery);

	List<WarningMessage> findWarningsByUser(Integer productId, Integer userId);

	List<WarningMail> warningMailSendQuery(Integer userId);

	List<Integer> distinctEmailRecivers();

	void updateWarningMessageSendStatus(Integer userId, Integer messageType);

	void updateWarningMailSendStatus(Integer to, Integer status);
}
