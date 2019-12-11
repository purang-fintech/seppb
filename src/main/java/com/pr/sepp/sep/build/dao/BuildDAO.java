package com.pr.sepp.sep.build.dao;

import com.pr.sepp.sep.build.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BuildDAO {

	int releasenoteCreate(ReleaseNote dataMap);

	int releasenoteUpdate(ReleaseNote dataMap);

	List<ReleaseNote> releasenoteQuery(Map<String, Object> dataMap);

	List<JenkinsParam> urlQuery(Map<String, Object> dataMap);

	List<Build> buildQuery(Integer noteId);

	void saveBuild(BuildHistory buildHistory);

	void update(BuildHistory buildHistory);

	Integer maxBuildVersion(String jobName);

	List<BuildHistory> buildHistories(@Param("noteId") Integer noteId, @Param("pageStart") Integer pageStart,
									  @Param("pageSize") Integer pageSize);

	void saveBuildFiles(@Param("buildFiles") List<BuildFile> buildFiles);

	void deleteBuildFiles(Integer noteId);

	List<BuildFile> listBuildFiles(@Param("noteId") Integer noteId, @Param("instances") List<String> instances);

	List<BuildInstance> allBuildInstances(Integer productId);

	Optional<BuildHistory> findBuildHistoryById(Integer id);

	List<BuildHistory> getBuildHistories(String jobName);
}
