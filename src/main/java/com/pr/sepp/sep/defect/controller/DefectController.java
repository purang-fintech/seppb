package com.pr.sepp.sep.defect.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pr.sepp.base.service.BaseQueryService;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.mgr.user.service.UserService;
import com.pr.sepp.sep.defect.model.Defect;
import com.pr.sepp.sep.defect.service.DefectService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.pr.sepp.common.constants.CommonParameter.*;

@RestController
@ResponseBody
public class DefectController {

	@Autowired
	private DefectService defectService;

	@Autowired
	private UserService userService;

	@Autowired
	BaseQueryService baseQueryService;

	@PostMapping(value = "/defect/query")
	public PageInfo<Defect> defectQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> userMap = new HashMap<>();
		userMap.put(PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<User> users = userService.userQuery(userMap);

		String status = request.getParameter(CommonParameter.STATUS);
		if (!StringUtils.isEmpty(status)) {
			dataMap.put("sts", Arrays.asList(status.split(",")));
		}
		dataMap.put(REQ_ID, request.getParameter(REQ_ID));
		dataMap.put(PRODUCT_ID, request.getParameter(PRODUCT_ID));
		dataMap.put(REL_ID, request.getParameter(REL_ID));
		dataMap.put(ID, request.getParameter(ID));
		dataMap.put(SUBMITTER, request.getParameter(SUBMITTER));
		dataMap.put(RESPONSER, request.getParameter(RESPONSER));
		dataMap.put("priority", request.getParameter("priority"));
		dataMap.put("influence", request.getParameter("influence"));
		dataMap.put("conciliator", request.getParameter("conciliator"));
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

	@PostMapping(value = "/defect/query_id")
	public List<Defect> defectQuery(@RequestParam(value = "defectId") String defectId) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(ID, defectId);
		return defectService.defectQuery(dataMap);
	}

	@PostMapping(value = "/defect/query_ncp")
	public int defectQueryNCP() {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put(PRODUCT_ID, String.valueOf(ParameterThreadLocal.getProductId()));
		return defectService.defectQueryNC(dataMap);
	}

	@PostMapping(value = "/defect/create")
	public int defectInfoCreate(@RequestBody Defect defect) {
		return defectService.defectInfoCreate(defect);
	}

	@PostMapping(value = "/defect/update")
	public int defectInfoUpdate(@RequestBody Defect defect) throws IllegalAccessException {
		return defectService.defectInfoUpdate(defect);
	}

	@PostMapping(value = "/defect/status_update")
	public int defectStatusUpdate(@RequestBody Defect defectRequestParam) {
		return defectService.defectStatusUpdate(defectRequestParam);
	}

	@PostMapping(value = "/defect/refused")
	public PageInfo<Defect> refusedDefectQuery(HttpServletRequest request) {
		Map<String, Object> dataMap = new HashMap<>();
		Map<String, Object> userMap = new HashMap<>();
		userMap.put(PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<User> users = userService.userQuery(userMap);

		dataMap.put(REQ_ID, request.getParameter(REQ_ID));
		dataMap.put(PRODUCT_ID, request.getParameter(PRODUCT_ID));
		dataMap.put(REL_ID, request.getParameter(REL_ID));
		dataMap.put(ID, request.getParameter(ID));
		dataMap.put(SUBMITTER, request.getParameter(SUBMITTER));
		dataMap.put(RESPONSER, request.getParameter(RESPONSER));
		dataMap.put("priority", request.getParameter("priority"));
		dataMap.put("influence", request.getParameter("influence"));
		dataMap.put("conciliator", request.getParameter("conciliator"));
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

		int pageNum = ParameterThreadLocal.getPageNum();
		int pageSize = ParameterThreadLocal.getPageSize();
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
