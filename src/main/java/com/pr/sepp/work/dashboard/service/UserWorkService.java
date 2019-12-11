package com.pr.sepp.work.dashboard.service;

import java.util.Map;

public interface UserWorkService {
	Map<String, Object> productReleaseQuery();

	Map<String, Object> productRequestQuery();

	Map<String, Object> productMissionQuery();

	Map<String, Object> productDefectQuery();

	Map<String, Object> productTestingQuery();

	Map<String, Object> productWarningQuery();

	Map<String, Object> productPipelineQuery();

	Map<String, Object> productCodeHealthyQuery();

	Map<String, Object> productCodeSecurityQuery();

	Map<String, Object> productAutotestQuery();

	Map<String, Object> productCoverageQuery();
}
