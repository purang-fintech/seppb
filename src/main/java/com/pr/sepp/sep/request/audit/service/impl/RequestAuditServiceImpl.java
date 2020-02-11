package com.pr.sepp.sep.request.audit.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.notify.message.model.Message;
import com.pr.sepp.notify.message.service.MessageService;
import com.pr.sepp.sep.request.audit.dao.RequestAuditDAO;
import com.pr.sepp.sep.request.audit.model.RequestAudit;
import com.pr.sepp.sep.request.audit.model.RequestAuditResult;
import com.pr.sepp.sep.request.audit.service.RequestAuditService;
import com.pr.sepp.sep.request.data.dao.RequestDAO;
import com.pr.sepp.sep.request.data.model.ProductRequirement;
import com.pr.sepp.sep.requirement.model.Requirement;
import com.pr.sepp.sep.requirement.service.RequirementService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class RequestAuditServiceImpl implements RequestAuditService {

	@Autowired
	public RequirementService requirementService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private RequestAuditDAO requestAuditDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private RequestDAO requestDAO;

	@Override
	public int requestAuditCreate(RequestAudit requestAudit) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		String msg = "产品需求 #" + requestAudit.getPrId() + " 提交审批";
		SEPPHistory history = new SEPPHistory();
		history.setObjType(21);
		history.setObjId(requestAudit.getPrId());
		history.setObjKey(CommonParameter.ID);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(1);
		history.setNewValue(String.valueOf(requestAudit.getId()));
		history.setOperComment(msg);
		historyService.historyInsert(history);

		List<Integer> messageTo = new ArrayList<>();
		String baseAuditor = requestAudit.getBaseAuditor();
		String leaderAuditor = requestAudit.getLeaderAuditor();
		String chiefAuditor = requestAudit.getChiefAuditor();

		// 通知对应的审批人
		if (StringUtils.isNotEmpty(baseAuditor)) {
			Arrays.asList(baseAuditor.split(",")).forEach(f -> messageTo.add(Integer.parseInt(f)));
		}
		if (StringUtils.isEmpty(baseAuditor) && StringUtils.isNotEmpty(leaderAuditor)) {
			Arrays.asList(leaderAuditor.split(",")).forEach(f -> messageTo.add(Integer.parseInt(f)));
		}
		if (StringUtils.isEmpty(baseAuditor) && StringUtils.isEmpty(leaderAuditor) && StringUtils.isNotEmpty(chiefAuditor)) {
			Arrays.asList(chiefAuditor.split(",")).forEach(f -> messageTo.add(Integer.parseInt(f)));
		}

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(21);
		message.setObjectId(requestAudit.getPrId());
		message.setTitle("产品需求已提交至您审批，请及时处理");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageTo);

		return requestAuditDAO.requestAuditCreate(requestAudit);
	}

	@Override
	public int requestAuditPush(int id, RequestAuditResult requestAuditResult) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, id);
		dataMap.put(CommonParameter.PRODUCT_ID, productId);
		RequestAudit requestAudit = requestAuditQuery(dataMap).get(0);

		if (StringUtils.isNotEmpty(requestAudit.getCompleteTime())) {
			return -1;
		}

		List<RequestAuditResult> baseResults = requestAudit.getBaseAuditResult();
		baseResults = null == baseResults ? new ArrayList<>() : baseResults;
		List<RequestAuditResult> leaderResults = requestAudit.getLeaderAuditResult();
		leaderResults = null == leaderResults ? new ArrayList<>() : leaderResults;
		List<RequestAuditResult> chiefResults = requestAudit.getChiefAuditResult();
		chiefResults = null == chiefResults ? new ArrayList<>() : chiefResults;

		String baseAuditor = requestAudit.getBaseAuditor();
		String leaderAuditor = requestAudit.getLeaderAuditor();
		String chiefAuditor = requestAudit.getChiefAuditor();
		String newAuditorStr = requestAuditResult.getNewChiefAuditor();

		String[] toBases = StringUtils.isEmpty(baseAuditor) ? new String[]{} : baseAuditor.split(",");
		String[] toLeaders = StringUtils.isEmpty(leaderAuditor) ? new String[]{} : leaderAuditor.split(",");
		String[] toChiefs = StringUtils.isEmpty(chiefAuditor) ? new String[]{} : chiefAuditor.split(",");
		String[] newAuditors = StringUtils.isEmpty(newAuditorStr) ? new String[]{} : newAuditorStr.split(",");
		if (toChiefs.length > 0) {
			System.arraycopy(toChiefs, 0, newAuditors, newAuditors.length - 1, toChiefs.length);
		}
		newAuditors = Arrays.stream(newAuditors).distinct().toArray(String[]::new);

		if (null != baseResults) {
			for (RequestAuditResult result : baseResults) {
				// ParameterThreadLocal.getUserId() 与 requestAuditResult.getAuditor()类型不同但效果等同
				if (result.getAuditor().equals(userId)) {
					return -1;    //已审批过
				}
			}
		}

		if (null != leaderResults) {
			for (RequestAuditResult result : leaderResults) {
				// ParameterThreadLocal.getUserId() 与 requestAuditResult.getAuditor()类型不同但效果等同
				if (result.getAuditor().equals(userId)) {
					return -1;    //已审批过
				}
			}
		}

		if (null != chiefResults) {
			for (RequestAuditResult result : chiefResults) {
				// ParameterThreadLocal.getUserId() 与 requestAuditResult.getAuditor()类型不同但效果等同
				if (result.getAuditor().equals(userId)) {
					return -1;    //已审批过
				}
			}
		}

		Map<String, Object> reqMap = new HashMap<>();
		reqMap.put(CommonParameter.ID, requestAudit.getPrId());
		reqMap.put(CommonParameter.PRODUCT_ID, productId);
		ProductRequirement request = requestDAO.requestQuery(reqMap).get(0);

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(21);
		message.setObjectId(requestAudit.getPrId());

		// 基础审批
		if (toBases.length > 0 && Arrays.asList(toBases).indexOf(String.valueOf(userId)) > -1) {
			if (requestAuditResult.getPassed() == 0) {    //审核拒绝则流程终止
				requestAuditComplete(requestAudit, 0, requestAuditResult.getAuditTime());
				List<Integer> messageTo = new ArrayList<>();
				String msg = "您提交的产品需求【" + request.getId() + " - " + request.getSummary() + "】已被审核拒绝，拒绝原因为：【"
						+ requestAuditResult.getAuditComment() + "】，详情请查看审核历史记录！";
				messageTo.add(request.getSubmitter());
				message.setTitle("产品需求已被审核拒绝");
				message.setContent(msg);
				messageService.businessMessageGenerator(message, userId, messageTo);
			} else {    // 基础审核全部通过，且后续审批人皆为空，直接结束流程，生成正式数据
				if (toLeaders.length == 0 && newAuditors.length == 0 && toBases.length == baseResults.size() + 1) {    //这种情况基本不存在，但是程序支持一下
					requestAuditComplete(requestAudit, 1, requestAuditResult.getAuditTime());

					List<Integer> messageTo = new ArrayList<>();
					String msg = "您提交的产品需求【" + request.getId() + " - " + request.getSummary() + "】已审核通过，详情请查看审核历史记录！";
					messageTo.add(request.getSubmitter());
					message.setTitle("产品需求已审核完成");
					message.setContent(msg);
					messageService.businessMessageGenerator(message, userId, messageTo);
				}
				List<Integer> messageTo = new ArrayList<>();
				String msg = "产品需求【" + request.getId() + " - " + request.getSummary() + "】前置审核已完成，结论皆为审核通过，请您继续审批！";
				if (toLeaders.length > 0) {
					Arrays.asList(toLeaders).forEach(f -> messageTo.add(Integer.parseInt(f)));
				} else if (toLeaders.length == 0 && newAuditors.length > 0) {
					Arrays.asList(newAuditors).forEach(f -> messageTo.add(Integer.parseInt(f)));
				}
				message.setTitle("产品需求已提交至您审批，请及时处理");
				message.setContent(msg);
				messageService.businessMessageGenerator(message, userId, messageTo);
			}
			return requestAuditDAO.requestBaseAuditPush(id, "[" + new Gson().toJson(requestAuditResult) + "]");
		}

		// 主管审批
		if (toLeaders.length > 0 && Arrays.asList(toLeaders).indexOf(String.valueOf(userId)) > -1) {
			StringBuffer sf = new StringBuffer();
			if (null != leaderResults && leaderResults.size() > 0) {
				leaderResults.forEach(res -> sf.append(new Gson().toJson(res) + ","));
			}
			sf.append(new Gson().toJson(requestAuditResult));
			if (requestAuditResult.getPassed() == 0) {    //审核拒绝则流程终止
				requestAuditComplete(requestAudit, 0, requestAuditResult.getAuditTime());
				List<Integer> messageTo = new ArrayList<>();
				String msg = "您提交的产品需求【" + request.getId() + " - " + request.getSummary() + "】已被审核拒绝，拒绝原因为：【"
						+ requestAuditResult.getAuditComment() + "】，详情请查看审核历史记录！";
				messageTo.add(request.getSubmitter());
				message.setTitle("产品需求已被审核拒绝");
				message.setContent(msg);
				messageService.businessMessageGenerator(message, userId, messageTo);
			} else {    // 主管审核全部通过，且后续审批人为空，直接结束流程，生成正式数据
				int resCount = null != leaderResults ? leaderResults.size() + 1 : 1;
				if (newAuditors.length == 0 && toLeaders.length == resCount) {
					requestAuditComplete(requestAudit, 1, requestAuditResult.getAuditTime());

					List<Integer> messageTo = new ArrayList<>();
					String msg = "您提交的产品需求【" + request.getId() + " - " + request.getSummary() + "】已审核通过，详情请查看审核历史记录！";
					messageTo.add(request.getSubmitter());
					message.setTitle("产品需求已审核完成");
					message.setContent(msg);
					messageService.businessMessageGenerator(message, userId, messageTo);
				} else if (newAuditors.length > 0 && toLeaders.length == resCount) {
					List<Integer> messageTo = new ArrayList<>();
					String msg = "产品需求【" + request.getId() + " - " + request.getSummary() + "】前置审核已完成，结论皆为审核通过，请您继续审批！";
					Arrays.asList(newAuditors).forEach(f -> messageTo.add(Integer.parseInt(f)));
					message.setTitle("产品需求已提交至您审批，请及时处理");
					message.setContent(msg);
					messageService.businessMessageGenerator(message, userId, messageTo);
				}
			}
			if (newAuditors.length > 0) {
				String newAuditorString = Arrays.deepToString(newAuditors).replace("[", "").replace("]", "");
				requestAuditDAO.requestAuditAppend(requestAudit.getId(), newAuditorString);
			}
			return requestAuditDAO.requestLeaderAuditPush(id, "[" + sf.toString() + "]");
		}

		// 高管审批
		if (toChiefs.length > 0 && Arrays.asList(toChiefs).indexOf(String.valueOf(userId)) > -1) {
			StringBuffer sf = new StringBuffer();
			if (null != chiefResults && chiefResults.size() > 0) {
				chiefResults.forEach(res -> sf.append(new Gson().toJson(res) + ","));
			}
			sf.append(new Gson().toJson(requestAuditResult));
			int chiefResultsSize = null != chiefResults ? chiefResults.size() + 1 : 1;
			if (requestAuditResult.getPassed() == 0) {    //审核拒绝则流程终止
				requestAuditComplete(requestAudit, 0, requestAuditResult.getAuditTime());
				List<Integer> messageTo = new ArrayList<>();
				String msg = "您提交的产品需求【" + request.getId() + " - " + request.getSummary() + "】已被审核拒绝，拒绝原因为：【"
						+ requestAuditResult.getAuditComment() + "】，详情请查看审核历史记录！";
				messageTo.add(request.getSubmitter());
				message.setTitle("产品需求已被审核拒绝");
				message.setContent(msg);
				messageService.businessMessageGenerator(message, userId, messageTo);
			} else {    // 高管审核全部通过，直接结束流程，生成正式数据
				if (toChiefs.length == chiefResultsSize) {
					requestAuditComplete(requestAudit, 1, requestAuditResult.getAuditTime());

					List<Integer> messageTo = new ArrayList<>();
					String msg = "您提交的产品需求【" + request.getId() + " - " + request.getSummary() + "】已审核通过，详情请查看审核历史记录！";
					messageTo.add(request.getSubmitter());
					message.setTitle("产品需求已审核完成");
					message.setContent(msg);
					messageService.businessMessageGenerator(message, userId, messageTo);
				}
			}
			return requestAuditDAO.requestChiefAuditPush(id, "[" + sf.toString() + "]");
		}
		return 0;
	}

	@Override
	public int requestAuditComplete(RequestAudit requestAudit, int passed, String completeTime) {
		if (passed == 1) {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put(CommonParameter.ID, requestAudit.getPrId());
			dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
			ProductRequirement request = requestDAO.requestQuery(dataMap).get(0);
			Requirement requirement = new Requirement();

			requirement.setType(request.getType());
			requirement.setSourceId(requestAudit.getPrId());
			requirement.setSubmitDate(request.getSubmitDate());
			requirement.setExpectDate(request.getExpectDate());
			requirement.setSitDate(null);
			requirement.setUatDate(null);
			requirement.setRelId(null);
			requirement.setStatus(1);
			requirement.setUiResource(request.getUiResource());
			requirement.setModuleId(request.getModuleId());
			requirement.setPriority(request.getPriority());
			requirement.setSubmitter(request.getSubmitter());
			requirement.setAttachment(request.getAttachment());
			requirement.setDetail(request.getDetail());
			requirement.setProductId(ParameterThreadLocal.getProductId());
			requirement.setSummary(request.getSummary());

			int formalId = requirementService.reqCreate(requirement, 0);
			requestDAO.requestStatusUpdate(requestAudit.getPrId(), 3);
			return requestAuditDAO.requestAuditComplete(requestAudit.getId(), formalId, completeTime);
		}
		requestDAO.requestStatusUpdate(requestAudit.getPrId(), 4);
		requestDAO.addAuditRefuseTimes(requestAudit.getPrId());
		return requestAuditDAO.requestAuditComplete(requestAudit.getId(), null, completeTime);
	}

	@Override
	public List<RequestAudit> requestAuditQuery(Map<String, Object> dataMap) {
		List<RequestAudit> requestAudits = requestAuditDAO.requestAuditQuery(dataMap);
		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<User> users = userDAO.userQuery(userMap);
		requestAudits.forEach(item -> {
			List<RequestAuditResult> baseAuditResult = new Gson().fromJson(item.getBaseAuditResultStr(), new TypeToken<List<RequestAuditResult>>() {
			}.getType());
			if (null != baseAuditResult && baseAuditResult.size() > 0) {
				baseAuditResult.forEach(base -> base.setAuditName(findUserName(users, base.getAuditor())));
				item.setBaseAuditResult(baseAuditResult);
			} else {
				item.setBaseAuditResult(null);
			}
			if (StringUtils.isNotEmpty(item.getBaseAuditor())) {
				StringBuilder sb = new StringBuilder();
				Arrays.asList(item.getBaseAuditor().split(",")).forEach(d -> {
					sb.append(findUserName(users, Integer.parseInt(d)));
					sb.append("，");
				});
				String auditorName = sb.toString();
				auditorName = auditorName.substring(0, auditorName.length() - 1);
				item.setBaseAuditorName(auditorName);
			}

			List<RequestAuditResult> leaderAuditResult = new Gson().fromJson(item.getLeaderAuditResultStr(), new TypeToken<List<RequestAuditResult>>() {
			}.getType());
			if (null != leaderAuditResult && leaderAuditResult.size() > 0) {
				leaderAuditResult.forEach(leader -> leader.setAuditName(findUserName(users, leader.getAuditor())));
				item.setLeaderAuditResult(leaderAuditResult);
			} else {
				item.setLeaderAuditResult(null);
			}
			if (StringUtils.isNotEmpty(item.getLeaderAuditor())) {
				StringBuilder sb = new StringBuilder();
				Arrays.asList(item.getLeaderAuditor().split(",")).forEach(d -> {
					sb.append(findUserName(users, Integer.parseInt(d)));
					sb.append("，");
				});
				String auditorName = sb.toString();
				auditorName = auditorName.substring(0, auditorName.length() - 1);
				item.setLeaderAuditorName(auditorName);
			}

			List<RequestAuditResult> chiefAuditResult = new Gson().fromJson(item.getChiefAuditResultStr(), new TypeToken<List<RequestAuditResult>>() {
			}.getType());
			if (null != chiefAuditResult && chiefAuditResult.size() > 0) {
				chiefAuditResult.forEach(chief -> chief.setAuditName(findUserName(users, chief.getAuditor())));
				item.setChiefAuditResult(chiefAuditResult);
			} else {
				item.setChiefAuditResult(null);
			}
			if (StringUtils.isNotEmpty(item.getChiefAuditor())) {
				StringBuilder sb = new StringBuilder();
				Arrays.asList(item.getChiefAuditor().split(",")).forEach(d -> {
					sb.append(findUserName(users, Integer.parseInt(d)));
					sb.append("，");
				});
				String auditorName = sb.toString();
				auditorName = auditorName.substring(0, auditorName.length() - 1);
				item.setChiefAuditorName(auditorName);
			}
		});
		return requestAudits;
	}

	private String findUserName(List<User> users, Integer uid) {
		if (null == uid) {
			return "";
		}
		return users.stream().filter(f -> Objects.equals(f.getUserId(), uid)).findFirst().orElse(new User()).getUserName();
	}
}
