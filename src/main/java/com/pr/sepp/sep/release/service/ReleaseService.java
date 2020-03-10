package com.pr.sepp.sep.release.service;

import com.pr.sepp.sep.release.model.Release;

import java.util.List;
import java.util.Map;

public interface ReleaseService {

	int releaseCreate(Release dataMap);

	int releaseUpdate(Release dataMap) throws IllegalAccessException;

	List<Release> releaseQuery(Map<String, Object> dataMap);

	List<Release> openReleaseQuery(Map<String, String> dataMap);

	int releaseUpdateDate(Release release);
}
