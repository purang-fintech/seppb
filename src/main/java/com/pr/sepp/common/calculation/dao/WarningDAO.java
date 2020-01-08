package com.pr.sepp.common.calculation.dao;

import com.pr.sepp.common.calculation.model.ReleaseSepData;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.sep.release.model.Release;

import java.time.LocalDate;
import java.util.List;

public interface WarningDAO {
	ReleaseSepData groupReleaseData(Integer relId, LocalDate warnDate);

	List<Product> warningProducts();

	List<Release> productOpenRelease(Integer productId);
}
