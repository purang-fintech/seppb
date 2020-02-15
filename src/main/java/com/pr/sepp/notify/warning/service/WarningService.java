package com.pr.sepp.notify.warning.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.common.calculation.model.ReleaseSepData;
import com.pr.sepp.common.calculation.model.WarningRules;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.notify.warning.dao.WarningDAO;
import com.pr.sepp.notify.warning.model.*;
import com.pr.sepp.sep.release.model.Release;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WarningService {

	@Autowired
	private WarningDAO warningDAO;

	public PageInfo<WarningMessage> warningListPaging(Integer productId, Integer userId, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<WarningMessage> warnings = warningDAO.findWarningsByUser(productId, userId);
		return new PageInfo<>(warnings);
	}

	public List<WarningMessage> warningQuery(WarningQuery warningQuery){
		return warningDAO.warningQuery(warningQuery);
	}

	public ReleaseSepData groupReleaseData(Integer relId, String warnDate) {
		return warningDAO.groupReleaseData(relId, warnDate);
	}

	public List<Product> warningProducts() {
		return warningDAO.warningProducts();
	}

	public List<WarningRules> warningRules() {
		return warningDAO.warningRules();
	}

	public List<Release> productOpenRelease(Integer productId) {
		return warningDAO.productOpenRelease(productId);
	}

	public Integer createBatchAndGetId(WarningBatch warningBatch) {
		warningDAO.createBatch(warningBatch);
		return warningBatch.getId();
	}

	public void createWarningBatch(List<Warning> warnings) {
		List<WarningNotify> warningNotifies = new ArrayList<>();

		warnings.forEach(warning -> {
			warningDAO.createWarning(warning);

			WarningNotify warningNotifyMesage = new WarningNotify();
			warningNotifyMesage.setTo(warning.getResponser());
			warningNotifyMesage.setWarningId(warning.getId());
			warningNotifyMesage.setSendGateway(1);
			warningNotifies.add(warningNotifyMesage);

			WarningNotify warningNotifyMail = new WarningNotify();
			warningNotifyMail.setTo(warning.getResponser());
			warningNotifyMail.setWarningId(warning.getId());
			warningNotifyMail.setSendGateway(2);
			warningNotifies.add(warningNotifyMail);
		});
		warningDAO.createWarningNotifyBatch(warningNotifies);
	}

	public void updateWarningMessageSendStatus(Integer userId, Integer messageType) {
		warningDAO.updateWarningMessageSendStatus(userId, messageType);
	}
}
