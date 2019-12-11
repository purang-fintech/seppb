package com.pr.sepp.sep.build.controller;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.sep.build.model.*;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.sep.build.model.req.BuildHistoryReq;
import com.pr.sepp.sep.build.model.req.DeploymentBuildReq;
import com.pr.sepp.sep.build.model.resp.BuildHistoryResp;
import com.pr.sepp.sep.build.service.BuildService;
import com.pr.sepp.sep.build.service.JenkinsBuildService;
import com.pr.sepp.sep.build.service.impl.BuildHistoryService;
import com.pr.sepp.utils.jenkins.model.ParameterDefinition;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
public class BuildController {

	@Autowired
	public BuildService buildService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private JenkinsBuildService jenkinsBuildService;

	@Autowired
	private BuildHistoryService buildHistoryService;

	/**
	 * 通过实例获取对应参数列表
	 *
	 * @param instance
	 * @return
	 */
	@GetMapping(value = "/build/params/{instance}")
	public List<String> params(@PathVariable("instance") String instance) {
		return jenkinsBuildService.jobParams(instance, ParameterThreadLocal.getProductId());
	}

	@GetMapping(value = "/build/params/jobs")
	public List<ParameterDefinition> jenkinsBuildParams(@RequestParam("jobName") String jobName, @RequestParam("instanceType") InstanceType type) {
		return jenkinsBuildService.getJobBuildParams(jobName, type);
	}

	/**
	 * jobname 进行构建
	 */
	@PostMapping(value = "/build/start-build/jobs")
	public void startBuild(@RequestBody @Valid DeploymentBuildReq deploymentBuildReq) {
		jenkinsBuildService.build(deploymentBuildReq);
	}

	/**
	 * 立即构建
	 *
	 * @param buildHistoryReq
	 */
	@PostMapping(value = "/build/start-build")
	public void startBuild(@RequestBody BuildHistoryReq buildHistoryReq) {
		jenkinsBuildService.build(buildHistoryReq);
	}

	/**
	 * 重试
	 */
	@PostMapping(value = "/build/retry-build/{id}")
	public void retryBuild(@PathVariable("id") Integer id) {
		jenkinsBuildService.retryBuild(id);
	}

	/**
	 * 获取对应功能分支构建列表
	 *
	 * @param noteId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping(value = "/build/build-history")
	public List<BuildHistoryResp> builds(@RequestParam("noteId") Integer noteId,
										 @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
										 @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize) {
		return buildHistoryService.buildHistories(noteId, pageNum, pageSize);
	}

	/**
	 * 获取构建日志
	 *
	 * @param jobName
	 * @param buildVersion
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/build/build-log")
	public Map<String, Object> buildLog(@RequestParam("jobName") String jobName,
										@RequestParam("buildVersion") Integer buildVersion,
										@RequestParam("instanceType") InstanceType instanceType) throws IOException {
		return buildHistoryService.buildLog(jobName, buildVersion, instanceType);
	}

	@GetMapping(value = "/build/build-instances")
	public List<BuildInstance> allBuildInstances() {
		return buildHistoryService.allBuildInstances(ParameterThreadLocal.getProductId());
	}

	@GetMapping(value = "/build/build-instances/{noteId}")
	public Map<String, List<BuildFile>> listBuildFiles(@PathVariable("noteId") Integer noteId) {
		return buildHistoryService.listBuildFiles(noteId, null);
	}

	@RequestMapping(value = "/build/releasenote_query", method = RequestMethod.POST)
	public PageInfo<ReleaseNote> releasenoteQuery(HttpServletRequest request) {

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("reqId", request.getParameter("reqId"));
		dataMap.put("relId", request.getParameter("relId"));
		String status = request.getParameter("status");
		if (!StringUtils.isEmpty(status)) {
			dataMap.put("status", Arrays.asList(status.split(",")));
		}
		String reqStatus = request.getParameter("reqStatus");
		if (!StringUtils.isEmpty(reqStatus)) {
			dataMap.put("reqStatus", Arrays.asList(reqStatus.split(",")));
		}
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("submitter", request.getParameter("submitter"));

		return buildService.releasenoteQuery(dataMap);
	}

	@RequestMapping(value = "/build/build_query/{noteId}", method = RequestMethod.POST)
	public PageInfo<Build> buildQuery(@PathVariable("noteId") Integer noteId) {

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("noteId", noteId);

		return buildService.buildQuery(noteId);
	}

	@RequestMapping(value = "/build/releasenote_create", method = RequestMethod.POST)
	public int releasenoteCreate(@RequestBody ReleaseNote note) {

		buildService.releasenoteCreate(note);
		int createdId = note.getId();

		SEPPHistory history = new SEPPHistory();
		history.setObjType(3);
		history.setObjId(createdId);
		history.setObjKey("id");
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(1);
		history.setNewValue(note.toString());
		history.setOperComment("创建出新的构建清单：【#" + createdId + " - " + note.getDescription() + "】");
		history.setReferUser(ParameterThreadLocal.getUserId());
		historyService.historyInsert(history);

		return createdId;
	}

	@RequestMapping(value = "/build/releasenote_update", method = RequestMethod.POST)
	public int releasenoteUpdate(@RequestBody ReleaseNote note) {
		return buildService.releasenoteUpdate(note);
	}

	@RequestMapping(value = "/build/query_url", method = RequestMethod.POST)
	public List<JenkinsParam> urlQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("prodBranch", request.getParameter("prodBranch"));
		dataMap.put("envType", request.getParameter("envType"));
		dataMap.put("instance", request.getParameter("instance"));
		return buildService.urlQuery(dataMap);
	}

}
