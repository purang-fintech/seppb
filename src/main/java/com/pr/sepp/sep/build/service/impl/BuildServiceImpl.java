package com.pr.sepp.sep.build.service.impl;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.sep.build.dao.BuildDAO;
import com.pr.sepp.sep.build.model.Build;
import com.pr.sepp.sep.build.model.BuildFile;
import com.pr.sepp.sep.build.model.JenkinsParam;
import com.pr.sepp.sep.build.model.ReleaseNote;
import com.pr.sepp.sep.build.service.BuildService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
@Transactional
public class BuildServiceImpl implements BuildService {

	@Autowired
	private BuildDAO buildDAO;

	@Autowired
	private UserDAO userDAO;

	@Override
	public PageInfo<ReleaseNote> releasenoteQuery(Map<String, Object> dataMap) {
		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());
		List<ReleaseNote> result = buildDAO.releasenoteQuery(dataMap);

		Map<String, Object> userMap = new HashMap<>();
		userMap.put("productId", ParameterThreadLocal.getProductId());
		List<User> users = userDAO.userQuery(userMap);

		result.forEach(item -> {
			String subName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getSubmitter())).findFirst().orElse(new User()).getUserName();
			String reqSuber = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getReqSubmitter())).findFirst().orElse(new User()).getUserName();
			String pdName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getPdResponser())).findFirst().orElse(new User()).getUserName();
			String devName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getDevResponser())).findFirst().orElse(new User()).getUserName();
			String testName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getTestResponser())).findFirst().orElse(new User()).getUserName();
			item.setSubmitterName(subName);
			item.setReqSubmitterName(reqSuber);
			item.setProderName(pdName);
			item.setDeverName(devName);
			item.setTesterName(testName);
		});

		PageInfo<ReleaseNote> pageInfo = new PageInfo<>(result);
		return pageInfo;
	}

	@Override
	public int releasenoteCreate(ReleaseNote releaseNote) {
		int count = buildDAO.releasenoteCreate(releaseNote);
		List<BuildFile> buildFiles = releaseNote.getBuildFiles();
		if(isNotEmpty(releaseNote.getBuildFiles())) {
			buildFiles.forEach(buildFile -> buildFile.setNoteId(releaseNote.getId()));
			buildDAO.saveBuildFiles(buildFiles);
		}
		return count;
	}

	@Override
	public int releasenoteUpdate(ReleaseNote dataMap) {
		buildDAO.deleteBuildFiles(dataMap.getId());
		if(isNotEmpty(dataMap.getBuildFiles())) {
            List<BuildFile> buildFiles = dataMap.getBuildFiles().stream().map(buildFile -> {
                buildFile.setNoteId(dataMap.getId());
                return buildFile;
            }).collect(Collectors.toList());
            buildDAO.saveBuildFiles(buildFiles);
		}
		return buildDAO.releasenoteUpdate(dataMap);
	}

	@Override
	public List<JenkinsParam> urlQuery(Map<String, Object> dataMap) {
		return buildDAO.urlQuery(dataMap);
	}

	@Override
	public PageInfo<Build> buildQuery(Integer noteId) {
		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());
		List<Build> result = buildDAO.buildQuery(noteId);
		PageInfo<Build> pageInfo = new PageInfo<>(result);

		return pageInfo;
	}
}
