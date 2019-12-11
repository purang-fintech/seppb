package com.pr.sepp.sep.defect.controller;

import com.pr.sepp.base.service.BaseQueryService;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.mgr.user.service.UserService;
import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.sep.defect.model.DefectRequestParam;
import com.pr.sepp.sep.defect.service.DefectService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@ResponseBody
public class DefectController {

	@Autowired
	private DefectService defectService;

	@Autowired
	private UserService userService;

	@Autowired
	BaseQueryService baseQueryService;

	@RequestMapping(value = "/defect/query", method = RequestMethod.POST)
	public PageInfo<Defect> defectQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("productId", ParameterThreadLocal.getProductId());
		List<User> users = userService.userQuery(userMap);

		String status = request.getParameter("status");
		if (!StringUtils.isEmpty(status)) {
			dataMap.put("sts", Arrays.asList(status.split(",")));
		}
		dataMap.put("reqId", request.getParameter("reqId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("priority", request.getParameter("priority"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("id", request.getParameter("id"));
		dataMap.put("influence", request.getParameter("influence"));
		dataMap.put("conciliator", request.getParameter("conciliator"));
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("prodModule", request.getParameter("prodModule"));
		dataMap.put("foundPeriod", request.getParameter("foundPeriod"));
		dataMap.put("defectPeriod", request.getParameter("defectPeriod"));
		dataMap.put("defectType", request.getParameter("defectType"));
		if (!StringUtils.isEmpty(request.getParameter("foundTimeBegin"))) {
			dataMap.put("foundTimeBegin", request.getParameter("foundTimeBegin") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("foundTimeEnd"))) {
			dataMap.put("foundTimeEnd", request.getParameter("foundTimeEnd") + " 23:59:59");
		}

		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());
		List<Defect> defects = defectService.defectQuery(dataMap);
		defects.forEach(item -> {
			String subName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getSubmitter())).findFirst().orElse(new User()).getUserName();
			String conName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getConciliator())).findFirst().orElse(new User()).getUserName();
			String resName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getResponser())).findFirst().orElse(new User()).getUserName();
			String proName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getProductor())).findFirst().orElse(new User()).getUserName();
			item.setSubmitterName(subName);
			item.setConciliatorName(conName);
			item.setResponserName(resName);
			item.setProductorName(proName);
		});
		PageInfo<Defect> pageInfo = new PageInfo<>(defects);
		return pageInfo;
	}

	@RequestMapping(value = "/defect/query_id", method = RequestMethod.POST)
	public List<Defect> defectQuery(@RequestParam(value = "defectId") String defectId) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("id", defectId);
		return defectService.defectQuery(dataMap);
	}

	@RequestMapping(value = "/defect/query_ncp", method = RequestMethod.POST)
	public int defectQueryNCP() {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("productId", String.valueOf(ParameterThreadLocal.getProductId()));
		return defectService.defectQueryNC(dataMap);
	}

	@RequestMapping(value = "/defect/create", method = RequestMethod.POST)
	public int defectInfoCreate(@RequestBody Defect defect) {
		return defectService.defectInfoCreate(defect);
	}

	@RequestMapping(value = "/defect/update", method = RequestMethod.POST)
	public int defectInfoUpdate(@RequestBody Defect defect) throws IllegalAccessException {
		return defectService.defectInfoUpdate(defect);
	}

	@RequestMapping(value = "/defect/status_update")
	public int defectStatusUpdate(@RequestBody Defect defectRequestParam) {
		return defectService.defectStatusUpdate(defectRequestParam);
	}

	@RequestMapping(value = "/defect/refused", method = RequestMethod.POST)
	public PageInfo<Defect> refusedDefectQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("productId", ParameterThreadLocal.getProductId());
		List<User> users = userService.userQuery(userMap);

		dataMap.put("reqId", request.getParameter("reqId"));
		dataMap.put("productId", request.getParameter("productId"));
		dataMap.put("priority", request.getParameter("priority"));
		dataMap.put("relId", request.getParameter("relId"));
		dataMap.put("id", request.getParameter("id"));
		dataMap.put("influence", request.getParameter("influence"));
		dataMap.put("conciliator", request.getParameter("conciliator"));
		dataMap.put("submitter", request.getParameter("submitter"));
		dataMap.put("responser", request.getParameter("responser"));
		dataMap.put("prodModule", request.getParameter("prodModule"));
		dataMap.put("foundPeriod", request.getParameter("foundPeriod"));
		dataMap.put("defectPeriod", request.getParameter("defectPeriod"));
		dataMap.put("defectType", request.getParameter("defectType"));
		if (!StringUtils.isEmpty(request.getParameter("foundTimeBegin"))) {
			dataMap.put("foundTimeBegin", request.getParameter("foundTimeBegin") + " 00:00:00");
		}
		if (!StringUtils.isEmpty(request.getParameter("foundTimeEnd"))) {
			dataMap.put("foundTimeEnd", request.getParameter("foundTimeEnd") + " 23:59:59");
		}

		int pageNum = StringUtils.isEmpty(request.getParameter("pageNum")) ? 1 : Integer.parseInt(request.getParameter("pageNum"));
		int pageSize = StringUtils.isEmpty(request.getParameter("pageSize")) ? 500 : Integer.parseInt(request.getParameter("pageSize"));
		PageHelper.startPage(pageNum, pageSize);

		List<Defect> defects = defectService.refusedDefectQuery(dataMap);
		defects.forEach(item -> {
			String subName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getSubmitter())).findFirst().orElse(new User()).getUserName();
			String conName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getConciliator())).findFirst().orElse(new User()).getUserName();
			String resName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getResponser())).findFirst().orElse(new User()).getUserName();
			String proName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getProductor())).findFirst().orElse(new User()).getUserName();
			item.setSubmitterName(subName);
			item.setConciliatorName(conName);
			item.setResponserName(resName);
			item.setProductorName(proName);
		});
		PageInfo<Defect> pageInfo = new PageInfo<>(defects);
		return pageInfo;
	}

}
