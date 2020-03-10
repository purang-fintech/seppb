package com.pr.sepp.sep.release.dao;

import com.pr.sepp.sep.release.model.Release;

import java.util.List;
import java.util.Map;

public interface ReleaseDAO {

	int releaseCreate(Release release);

	int releaseUpdate(Release release);

	List<Release> releaseQuery(Map<String, Object> dataMap);

	List<Release> openReleaseQuery(Map<String, String> dataMap);

	int releaseUpdateDate(Release release);

}
