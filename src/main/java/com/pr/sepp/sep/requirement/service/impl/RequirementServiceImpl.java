package com.pr.sepp.sep.requirement.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pr.sepp.base.dao.BaseQueryDAO;
import com.pr.sepp.base.model.RequirementStatus;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.file.dao.FileDAO;
import com.pr.sepp.file.model.SEPPFile;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.module.dao.ModuleDAO;
import com.pr.sepp.mgr.module.model.Module;
import com.pr.sepp.mgr.product.model.ProductDoc;
import com.pr.sepp.mgr.product.service.ProductService;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.notify.message.model.Message;
import com.pr.sepp.notify.message.service.MessageService;
import com.pr.sepp.sep.change.model.Change;
import com.pr.sepp.sep.change.service.ChangeService;
import com.pr.sepp.sep.coding.service.CodeMissionService;
import com.pr.sepp.sep.problem.dao.ProblemDAO;
import com.pr.sepp.sep.release.dao.ReleaseDAO;
import com.pr.sepp.sep.release.model.Release;
import com.pr.sepp.sep.requirement.dao.RequirementDAO;
import com.pr.sepp.sep.requirement.model.ReqRelease;
import com.pr.sepp.sep.requirement.model.ReqStatusUpdate;
import com.pr.sepp.sep.requirement.model.Requirement;
import com.pr.sepp.sep.requirement.service.RequirementService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class RequirementServiceImpl implements RequirementService {

	@Autowired
	private HistoryService historyService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private RequirementDAO requirementDAO;

	@Autowired
	private ReleaseDAO releaseDAO;

	@Autowired
	private ModuleDAO moduleDAO;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private ProblemDAO problemDAO;

	@Autowired
	private FileDAO fileDAO;

	@Autowired
	private BaseQueryDAO baseQueryDAO;

	@Autowired
	private ChangeService changeService;

	@Autowired
	private CodeMissionService codeMissionService;

	@Override
	public int reqCreate(Requirement requirement, Integer isProblem) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		if (requirement.getType() == 4) {
			requirement.setType(1);
		}

		requirement.setProductId(productId);

		requirementDAO.reqCreate(requirement);
		int createdId = requirement.getId();


		//创建产品文档 for 产品文档整理/归档用
		if (!StringUtils.isEmpty(requirement.getAttachment())) {
			String[] attachs = requirement.getAttachment().split(",");

			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put("files", Arrays.asList(attachs));
			List<SEPPFile> fileList = fileDAO.attachQuery(fileMap);

			fileList.forEach(file -> {
				ProductDoc doc = new ProductDoc();
				doc.setAttachmentId(file.getId());
				doc.setLabel(file.getFileName());
				doc.setModuleId(requirement.getModuleId());
				doc.setParentId(requirement.getModuleId());
				doc.setProductId(productId);
				doc.setType("doc");
				doc.setMaintainUser(userId);
				productService.productDocCreate(doc);
			});
		}

		if (isProblem == 1) {
			Map<String, String> problemMap = new HashMap<>();
			problemMap.put(CommonParameter.ID, String.valueOf(requirement.getSourceId()));
			problemMap.put("transId", String.valueOf(createdId));
			problemDAO.problemRelate(problemMap);
		}

		String prefix = requirement.getType() == 3 ? "" : "产品需求初审通过，";

		String msg = prefix + "创建正式产品研发需求：【#" + createdId + " - " + requirement.getSummary() + "】";
		SEPPHistory history = new SEPPHistory();
		history.setObjType(2);
		history.setObjId(createdId);
		history.setObjKey(CommonParameter.ID);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(1);
		history.setNewValue(String.valueOf(createdId));
		history.setOperComment(msg);
		historyService.historyInsert(history);

		List<Integer> messageTo = new ArrayList<>();

		// 通知对应模块的开发、测试、产品负责人以及项目经理
		Map<String, Object> moduleMap = new HashMap<>();
		moduleMap.put(CommonParameter.MODULE_ID, requirement.getModuleId());
		Module module = moduleDAO.moduleQuery(moduleMap).get(0);
		messageTo.add(module.getPdResponser());
		messageTo.add(module.getDevResponser());
		messageTo.add(module.getTestResponser());
		// 项目经理
		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.PRODUCT_ID, productId);
		userMap.put("roleId", "10");
		List<User> pms = userDAO.userQueryProductRole(userMap);
		pms.forEach(user -> messageTo.add(user.getUserId()));

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(2);
		message.setObjectId(createdId);
		message.setTitle(prefix + "可正式进入开发流程");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageTo);

		return createdId;
	}

	@Override
	public int reqUpdate(Requirement requirement, int reqChanged) throws IllegalAccessException {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int reqId = requirement.getId();
		int moduleId = requirement.getModuleId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Change change = new Change();

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put(CommonParameter.ID, reqId);
		queryMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		Requirement oldReq = requirementDAO.reqQuery(queryMap).get(0);

		change.setReqId(reqId);
		change.setChangeStatus(1);
		change.setChangeUser(userId);
		change.setChangeTime(sdf.format(System.currentTimeMillis()));
		change.setAuditUser(changeService.changeAuditorQuery(productId, reqId));
		change.setAudittedUser(null);
		change.setChangeDesc(requirement.getChangeDesc());
		change.setChangeDetail(requirement.getChangeDetail());

		requirement.setProductId(oldReq.getProductId());
		requirement.setSubmitDate(oldReq.getSubmitDate());
		requirement.setSubmitter(oldReq.getSubmitter());
		requirement.setSitDate(oldReq.getSitDate());
		requirement.setUatDate(oldReq.getUatDate());
		requirement.setSourceId(oldReq.getSourceId());
		requirement.setPdResponser(oldReq.getPdResponser());
		requirement.setDevResponser(oldReq.getDevResponser());
		requirement.setTestResponser(oldReq.getTestResponser());

		if (requirement.getStatus() == 0) {
			requirement.setCloseStyle(Objects.equals(oldReq.getCloseStyle(), null) ? requirement.getCloseStyle() : oldReq.getCloseStyle());
		}

		Map<String, Object> operMap = new HashMap<>();
		operMap.put(CommonParameter.USER_ID, userId);
		operMap.put(CommonParameter.PRODUCT_ID, productId);
		String operName = userDAO.userQuery(operMap).get(0).getUserName();
		String msg = "产品需求：【#" + reqId + " - " + requirement.getSummary() + "】信息由用户【" + operName + "】完成编辑";

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(2);
		message.setObjectId(reqId);
		message.setTitle("产品需求信息修改提示");

		int res = requirementDAO.reqUpdate(requirement);
		if (reqChanged > 0) {
			changeService.changeInfoCreate(change);
			List<Integer> messageToAud = new ArrayList<>();
			String auditors = changeService.changeAuditorQuery(productId, reqId);
			if (null != auditors) {
				Arrays.asList(auditors.split(",")).forEach(item -> {
					messageToAud.add(Integer.parseInt(item));
				});
			}
			String suffix = "，编辑内容已触发需求变更流程，作为变更审批人，您需要尽快到【需求变更】页面确认该变更的详情！";
			message.setContent(msg + suffix);
			messageService.businessMessageGenerator(message, userId, messageToAud);
		} else {    //非变更流程，只通知相关负责人
			List<Integer> messageToSub = new ArrayList<>();
			messageToSub.add(oldReq.getSubmitter());
			message.setContent("您提交的" + msg);
			messageService.businessMessageGenerator(message, userId, messageToSub);
		}

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends Requirement> cls = requirement.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(requirement);
			Object oldValue = field.get(oldReq);

			if (keyName.endsWith("Name") || keyName.equals(CommonParameter.REL_CODE) || keyName.equals("cmCount") || keyName.startsWith("change")) {
				continue;
			}
			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(2);
				history.setObjId(reqId);
				history.setProductId(productId);
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment(msg);
				history.setReferUser(oldReq.getSubmitter());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		if (!StringUtils.isEmpty(requirement.getAttachment())) {        //变更之后的附件不为空
			String[] attachs = requirement.getAttachment().split(",");

			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put("files", Arrays.asList(attachs));
			List<SEPPFile> fileList = fileDAO.attachQuery(fileMap);
			ProductDoc doc = new ProductDoc();
			if (StringUtils.isEmpty(oldReq.getAttachment())) {        //原有附件为空
				for (SEPPFile file : fileList) {        //逐个创建产品整理所需文档
					doc.setAttachmentId(file.getId());
					doc.setLabel(file.getFileName());
					doc.setProductId(productId);
					doc.setParentId(moduleId);
					doc.setType("doc");
					doc.setMaintainUser(userId);
					doc.setModuleId(moduleId);
					productService.productDocCreate(doc);
				}
			} else {        //原有附件也不为空
				String[] oldAttachs = oldReq.getAttachment().split(",");
				List<String> oldAttachList = Arrays.asList(oldAttachs);
				List<String> newAttachList = Arrays.asList(attachs);
				Set<Integer> moduleChanged = new HashSet<>();
				for (SEPPFile file : fileList) {        //原有附件中不包含的附件——新附件
					if (oldAttachList.indexOf(file.getId() + "") >= 0) {
						if (oldReq.getModuleId() - moduleId != 0) {
							moduleChanged.add(file.getId());
						}
						continue;
					}
					//逐个创建产品整理所需文档
					doc.setAttachmentId(file.getId());
					doc.setLabel(file.getFileName());
					doc.setProductId(productId);
					doc.setParentId(moduleId);
					doc.setType("doc");
					doc.setMaintainUser(userId);
					doc.setModuleId(moduleId);
					productService.productDocCreate(doc);
				}
				for (int i = 0; i < oldAttachs.length; i++) {        //新附件里面不包含的原有附件——变更删除的附件
					if (newAttachList.indexOf(oldAttachs[i]) >= 0) {
						if (oldReq.getModuleId() - moduleId != 0) {
							moduleChanged.add(Integer.parseInt(oldAttachs[i]));
						}
						continue;
					}
					//逐条删除因为变更所需要删除的附件
					productService.productDocDelete(Integer.parseInt(oldAttachs[i]));
				}

				//需求变更改变了所属模块，产品文档所属模块同步更新到新所属模块的根目录下
				Iterator<Integer> it = moduleChanged.iterator();
				while (it.hasNext()) {
					int attachId = it.next();
					Map<String, Object> docMap = new HashMap<>();
					docMap.put(CommonParameter.ID, attachId);
					docMap.put(CommonParameter.PRODUCT_ID, productId);
					List<ProductDoc> docs = productService.productDocQuery(docMap);
					if (null == docs || docs.size() == 0) {
						continue;
					}
					ProductDoc pdoc = docs.get(0);
					pdoc.setParentId(moduleId);
					productService.productDocUpdate(pdoc);
				}
			}
		} else {        //变更之后的附件为空
			if (!StringUtils.isEmpty(oldReq.getAttachment())) {        //变更之前的附件不为空
				String[] oldAttachs = oldReq.getAttachment().split(",");
				for (int i = 0; i < oldAttachs.length; i++) {    //删除原有附件
					productService.productDocDelete(Integer.parseInt(oldAttachs[i]));
				}
			}
		}
		return res;
	}

	@Override
	public int reqStatusUpdate(ReqStatusUpdate reqStatusUpdate) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		int id = reqStatusUpdate.getId();
		int status = reqStatusUpdate.getStatus();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, id);
		dataMap.put(CommonParameter.PRODUCT_ID, productId);
		Requirement requirement = requirementDAO.reqQuery(dataMap).get(0);

		dataMap.put(CommonParameter.STATUS, status);
		List<RequirementStatus> sts = baseQueryDAO.requirementStatus();
		String newStatusName = sts.stream().filter(f -> f.getStatusId() == status).findFirst().orElse(new RequirementStatus()).getStatusName();
		String oldStatusName = sts.stream().filter(f -> Objects.equals(f.getStatusId(), requirement.getStatus())).findFirst().orElse(new RequirementStatus()).getStatusName();

		String msg = "产品需求：【#" + id + " - " + requirement.getSummary() + "】，状态从【" + oldStatusName + "】变为【" + newStatusName + "】";
		SEPPHistory history = new SEPPHistory();
		history.setObjType(2);
		history.setObjId(id);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(2);
		history.setOperComment(msg);
		history.setReferUser(requirement.getSubmitter());
		history.setOrgValue(String.valueOf(requirement.getStatus()));
		history.setNewValue(String.valueOf(status));
		history.setObjKey(CommonParameter.STATUS);
		historyService.historyInsert(history);

		List<Integer> messageToSub = new ArrayList<>();
		messageToSub.add(requirement.getSubmitter());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(2);
		message.setObjectId(id);
		message.setTitle("产品需求状态更新提示");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageToSub);

		return requirementDAO.reqStatusUpdate(reqStatusUpdate);
	}

	@Override
	public int reqRelease(String mappedJson) {
		List<ReqRelease> releasing = new Gson().fromJson(mappedJson, new TypeToken<List<ReqRelease>>() {
		}.getType());
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		List<SEPPHistory> histories = new ArrayList<>();

		releasing.forEach(req -> {
			Map<String, Object> reqMap = new HashMap<>();
			reqMap.put(CommonParameter.ID, req.getId());
			reqMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
			Requirement requirement = requirementDAO.reqQuery(reqMap).get(0);

			Map<String, Object> relMap = new HashMap<>();
			relMap.put(CommonParameter.ID, req.getRelId());
			relMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
			Release release = releaseDAO.releaseQuery(relMap).get(0);
			String msg = "产品需求：【#" + req.getId() + " - " + requirement.getSummary() + "】已纳入版本【" + release.getRelCode() + "】";

			List<Integer> messageTo = new ArrayList<>();
			messageTo.add(requirement.getSubmitter());

			// 通知对应模块的开发、测试、产品负责人以及项目经理
			Map<String, Object> moduleMap = new HashMap<>();
			moduleMap.put(CommonParameter.MODULE_ID, requirement.getModuleId());
			Module module = moduleDAO.moduleQuery(moduleMap).get(0);
			messageTo.add(module.getPdResponser());
			messageTo.add(module.getDevResponser());
			messageTo.add(module.getTestResponser());
			// 项目经理
			Map<String, Object> userMap = new HashMap<>();
			userMap.put(CommonParameter.PRODUCT_ID, productId);
			userMap.put("roleId", "10");
			List<User> pms = userDAO.userQueryProductRole(userMap);
			pms.forEach(user -> messageTo.add(user.getUserId()));

			Message message = new Message();
			message.setProductId(productId);
			message.setObjectType(2);
			message.setObjectId(req.getId());
			message.setTitle("产品需求纳入版本提示");
			message.setContent(msg);
			messageService.businessMessageGenerator(message, userId, messageTo);

			SEPPHistory history = new SEPPHistory();
			history.setObjType(2);
			history.setObjId(req.getId());
			history.setProductId(productId);
			history.setOperUser(userId);
			history.setOperType(2);
			history.setOperComment(msg);
			history.setReferUser(requirement.getSubmitter());
			history.setOrgValue(null);
			history.setNewValue(String.valueOf(req.getRelId()));
			history.setObjKey("rel_id");
			histories.add(history);
		});

		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("releasing", releasing);

		requirementDAO.reqDefectRelease(dataMap);
		return requirementDAO.reqRelease(dataMap);
	}

	@Override
	public int reqUnRelease(List<String> reqs) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();
		List<SEPPHistory> histories = new ArrayList<>();

		reqs.forEach(reqId -> {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put(CommonParameter.ID, reqId);
			dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
			Requirement requirement = requirementDAO.reqQuery(dataMap).get(0);
			String msg = "产品需求：【#" + reqId + " - " + requirement.getSummary() + "】已从版本【" + requirement.getRelCode() + "】中移除";

			List<Integer> messageTo = new ArrayList<>();
			messageTo.add(requirement.getSubmitter());

			// 通知对应模块的开发、测试、产品负责人以及项目经理
			Map<String, Object> moduleMap = new HashMap<>();
			moduleMap.put(CommonParameter.MODULE_ID, requirement.getModuleId());
			Module module = moduleDAO.moduleQuery(moduleMap).get(0);
			messageTo.add(module.getPdResponser());
			messageTo.add(module.getDevResponser());
			messageTo.add(module.getTestResponser());
			// 项目经理
			Map<String, Object> userMap = new HashMap<>();
			userMap.put(CommonParameter.PRODUCT_ID, productId);
			userMap.put("roleId", "10");
			List<User> pms = userDAO.userQueryProductRole(userMap);
			pms.forEach(user -> messageTo.add(user.getUserId()));

			Message message = new Message();
			message.setProductId(productId);
			message.setObjectType(2);
			message.setObjectId(Integer.valueOf(reqId));
			message.setTitle("产品需求移出版本提示");
			message.setContent(msg);
			messageService.businessMessageGenerator(message, userId, messageTo);

			SEPPHistory history = new SEPPHistory();
			history.setObjType(2);
			history.setObjId(Integer.valueOf(reqId));
			history.setProductId(productId);
			history.setOperUser(userId);
			history.setOperType(2);
			history.setOperComment(msg);
			history.setReferUser(requirement.getSubmitter());
			history.setOrgValue(String.valueOf(requirement.getRelId()));
			history.setNewValue(null);
			history.setObjKey("rel_id");
			histories.add(history);
		});

		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("reqs", reqs);

		requirementDAO.reqDefectUnRelease(dataMap);
		return requirementDAO.reqUnRelease(dataMap);
	}

	@Override
	public List<Requirement> reqQuery(Map<String, Object> dataMap) {
		PageHelper.startPage(ParameterThreadLocal.getPageNum(), ParameterThreadLocal.getPageSize());

		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<Requirement> list = requirementDAO.reqQuery(dataMap);
		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<User> users = userDAO.userQuery(userMap);
		list.forEach(item -> {
			String subName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getSubmitter())).findFirst().orElse(new User()).getUserName();
			String pdName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getPdResponser())).findFirst().orElse(new User()).getUserName();
			String devName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getDevResponser())).findFirst().orElse(new User()).getUserName();
			String testName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getTestResponser())).findFirst().orElse(new User()).getUserName();
			item.setSubmitterName(subName);
			item.setProderName(pdName);
			item.setDeverName(devName);
			item.setTesterName(testName);
		});

		return list;
	}

	@Override
	public List<Requirement> relReqQuery(Integer relId, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		List<Requirement> list = requirementDAO.relReqQuery(relId);
		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<User> users = userDAO.userQuery(userMap);
		list.forEach(item -> {
			String subName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getSubmitter())).findFirst().orElse(new User()).getUserName();
			String pdName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getPdResponser())).findFirst().orElse(new User()).getUserName();
			String devName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getDevResponser())).findFirst().orElse(new User()).getUserName();
			String testName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getTestResponser())).findFirst().orElse(new User()).getUserName();
			item.setSubmitterName(subName);
			item.setProderName(pdName);
			item.setDeverName(devName);
			item.setTesterName(testName);
		});
		return list;
	}

	@Override
	public List<Requirement> reqBatchQuery(Map<String, Object> dataMap) {
		List<Requirement> list = requirementDAO.reqBatchQuery(dataMap);
		Map<String, Object> userMap = new HashMap<>();
		userMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		List<User> users = userDAO.userQuery(userMap);
		list.forEach(item -> {
			String subName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getSubmitter())).findFirst().orElse(new User()).getUserName();
			String pdName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getPdResponser())).findFirst().orElse(new User()).getUserName();
			String devName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getDevResponser())).findFirst().orElse(new User()).getUserName();
			String testName = users.stream().filter(u -> Objects.equals(u.getUserId(), item.getTestResponser())).findFirst().orElse(new User()).getUserName();
			item.setSubmitterName(subName);
			item.setProderName(pdName);
			item.setDeverName(devName);
			item.setTesterName(testName);
		});

		return list;
	}

	@Override
	public List<Map<String, String>> reqHistoryQuery(Integer reqId) {
		return requirementDAO.reqHistoryQuery(reqId);
	}

	@Override
	public int reqDelayCopy(Integer oldReqId) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put(CommonParameter.ID, oldReqId);
		dataMap.put(CommonParameter.PRODUCT_ID, ParameterThreadLocal.getProductId());
		Requirement requirement = requirementDAO.reqQuery(dataMap).get(0);

		ReqStatusUpdate reqStatusUpdate = new ReqStatusUpdate();
		reqStatusUpdate.setId(oldReqId);
		reqStatusUpdate.setStatus(0);
		reqStatusUpdate.setCloseStyle(4);

		requirementDAO.reqStatusUpdate(reqStatusUpdate);

		requirement.setStatus(1);
		if (requirement.getType() != 5) {
			requirement.setSourceId(oldReqId);
		}
		requirement.setType(5);

		requirementDAO.reqCreate(requirement);
		int newReqId = requirement.getId();
		// 将原需求下未关闭缺陷挪到新需求下，版本号置为空，注意后续需求纳入版本时需要将缺陷所属版本补齐
		requirementDAO.moveReqDefectNotClosed(newReqId, oldReqId);
		// 将原需求下未完成开发任务挪到新需求下
		int ncMissionCount = requirementDAO.moveReqCmsNotCompleted(newReqId, oldReqId);
		if (ncMissionCount > 0) {
			ReqStatusUpdate newStatusUpdate = new ReqStatusUpdate();
			newStatusUpdate.setId(newReqId);
			newStatusUpdate.setStatus(3);
			requirementDAO.reqStatusUpdate(newStatusUpdate);
		}
		// 将原需求下已完成开发任务尽数关闭
		codeMissionService.reqCmsStatusSync(oldReqId, 0);

		String msg = "产品需求：【#" + oldReqId + " - " + requirement.getSummary() + "】，因延期而关闭，并且衍生新的待开发需求 #" + newReqId;
		SEPPHistory history = new SEPPHistory();
		history.setObjType(2);
		history.setObjId(oldReqId);
		history.setProductId(productId);
		history.setOperUser(userId);
		history.setOperType(2);
		history.setOperComment(msg);
		history.setReferUser(requirement.getSubmitter());
		history.setOrgValue(String.valueOf(requirement.getStatus()));
		history.setNewValue("0");
		history.setObjKey(CommonParameter.STATUS);
		historyService.historyInsert(history);

		List<Integer> messageToSub = new ArrayList<>();
		messageToSub.add(requirement.getSubmitter());
		messageToSub.add(requirement.getPdResponser());
		messageToSub.add(requirement.getDevResponser());
		messageToSub.add(requirement.getTestResponser());

		Message message = new Message();
		message.setProductId(productId);
		message.setObjectType(2);
		message.setObjectId(oldReqId);
		message.setTitle("产品需求延期复制提示");
		message.setContent(msg);
		messageService.businessMessageGenerator(message, userId, messageToSub);

		return 0;
	}

}
