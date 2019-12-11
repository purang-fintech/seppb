package com.pr.sepp.sep.build.service;

import com.pr.sepp.sep.build.model.Build;
import com.pr.sepp.sep.build.model.JenkinsParam;
import com.pr.sepp.sep.build.model.ReleaseNote;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface BuildService {

	PageInfo<ReleaseNote> releasenoteQuery(Map<String, Object> dataMap);

	int releasenoteCreate(ReleaseNote dataMap);

	int releasenoteUpdate(ReleaseNote dataMap);

	List<JenkinsParam> urlQuery(Map<String, Object> dataMap);

	PageInfo<Build> buildQuery(Integer noteId);

}
