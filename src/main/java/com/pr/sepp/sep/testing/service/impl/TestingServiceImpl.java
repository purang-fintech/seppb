package com.pr.sepp.sep.testing.service.impl;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.history.model.SEPPHistory;
import com.pr.sepp.history.service.HistoryService;
import com.pr.sepp.mgr.module.dao.ModuleDAO;
import com.pr.sepp.mgr.module.model.Module;
import com.pr.sepp.mgr.product.dao.ProductDAO;
import com.pr.sepp.mgr.product.model.Product;
import com.pr.sepp.sep.requirement.dao.RequirementDAO;
import com.pr.sepp.sep.requirement.model.Requirement;
import com.pr.sepp.sep.testing.dao.TestingDAO;
import com.pr.sepp.sep.testing.model.*;
import com.pr.sepp.sep.testing.service.TestingService;
import com.pr.sepp.utils.CSVFileUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Transactional
@Service("testingService")
public class TestingServiceImpl implements TestingService {

	@Autowired
	private TestingDAO testingDAO;

	@Autowired
	private ModuleDAO moduleDAO;

	@Autowired
	private ProductDAO productDAO;

	@Autowired
	private RequirementDAO requirementDAO;

	@Autowired
	private HistoryService historyService;

	private final static String RMARK = UUID.randomUUID().toString();
	private final static String COMMA = "/,";

	@Override
	public List<CaseFolder> treeQuery(Map<String, Object> dataMap) {
		return testingDAO.treeQuery(dataMap);
	}

	@Override
	public Map<String, Object> caseUpload(String filePath) {
		List<Map<String, Object>> messages = new ArrayList<>();
		List<CaseInfo> cases = new ArrayList<>();
		Map<String, Object> uploadResult = new HashMap<>();

		List<String[]> fileContent = lineDataHandle(filePath);
		if (null == fileContent || fileContent.size() == 0) {
			Map<String, Object> message = new HashMap<>();
			message.put("message", "上载文件格式错误或内容为空");
			messages.add(message);
			uploadResult.put("messages", messages);
			uploadResult.put("cases", cases);
			return uploadResult;
		}

		int productId;
		int moduleId;
		String lastCaseFolder = "";
		String lastCaseName = "";
		int lastIndex = -1;
		int lastCaseId = -1;

		for (int i = 0; i < fileContent.size(); i++) {
			String[] oldItem = fileContent.get(i);
			String[] item = new String[oldItem.length];
			for (int n = 0; n < oldItem.length; n++) {
				if (StringUtils.isEmpty(oldItem[n])) {
					Arrays.fill(item, n, n + 1, "");
				} else {
					String newItem = oldItem[n].indexOf(COMMA) > -1 ? oldItem[n].replaceAll(COMMA, ",") : oldItem[n];
					if (newItem.startsWith("\"") && newItem.endsWith("\"")) {
						newItem = newItem.replaceAll("\"", "");
					}
					Arrays.fill(item, n, n + 1, newItem);
				}
			}
			Map<String, Object> qryMap = new HashMap<>();
			Map<String, Object> message = new HashMap<>();
			if (!StringUtils.isEmpty(item[0])) {
				if (lastCaseFolder.equalsIgnoreCase(item[2]) && lastCaseName.equalsIgnoreCase(item[3])) {
					message.put("message", "第" + (i + 1) + "行与第" + lastIndex + "行，相同目录下测试用例名称重复");
					messages.add(message);
					continue;
				}
				lastCaseFolder = item[2];
				lastCaseName = item[3];
				lastIndex = i + 1;
				qryMap.put("productNameFull", item[0]);
				List<Product> prodcut = productDAO.productQuery(qryMap);
				if (null == prodcut || prodcut.size() == 0) {
					message.put("message", "第" + (i + 1) + "行：产品名称错误");
					messages.add(message);
					continue;
				}
				productId = prodcut.get(0).getProductId();
				if (productId - ParameterThreadLocal.getProductId() != 0) {
					message.put("message", "第" + (i + 1) + "行：您没有本产品操作权限");
					messages.add(message);
					continue;
				}

				qryMap.put("productId", productId);
				qryMap.put("moduleNameFull", item[1]);
				List<Module> module = moduleDAO.moduleQuery(qryMap);
				if (null == module || module.size() == 0) {
					message.put("message", "第" + (i + 1) + "行：产品模块名称错误");
					messages.add(message);
					continue;
				}
				moduleId = module.get(0).getModuleId();
				String[] folders = item[2].replaceAll("\\\\", "/").split("/");
				if (folders.length > 5) {
					message.put("message", "第" + (i + 1) + "行：目录层次超过5层，请重新组织");
					messages.add(message);
					continue;
				}
				Map<String, Object> caseQryMap = new HashMap<>();
				//上传默认父目录为根目录
				int parentId = 1;
				boolean parentExist = false;
				for (int j = 0; j < folders.length; j++) {
					caseQryMap.put("productId", productId);
					caseQryMap.put("folderName", folders[j]);
					caseQryMap.put("parentId", parentId);
					caseQryMap.put("type", "folder");
					// 查询指定目录是否已经存在，已确定父目录不存在，子目录无需再查询
					Integer folderId = null;
					if (!parentExist) {
						folderId = testingDAO.caseFolderQuery(caseQryMap);
					} else {
						folderId = null;
					}
					// 不存在的目录，需要当下建立
					if (null == folderId || folderId <= 0) {
						CaseFolder caseFolder = new CaseFolder();
						caseFolder.setName(folders[j]);
						caseFolder.setParentId(parentId);
						caseFolder.setProductId(productId);
						caseFolder.setType("folder");
						caseFolder.setCreator(ParameterThreadLocal.getUserId());

						// 记录新创建的父目录
						testingDAO.caseFolderCreate(caseFolder);
						parentId = caseFolder.getId();
						parentExist = true;
					} else {
						parentId = folderId;
					}
				}
				caseQryMap.put("productId", productId);
				caseQryMap.put("folderName", item[3]);
				caseQryMap.put("parentId", parentId);
				caseQryMap.put("type", "case");
				Integer existCase = testingDAO.caseFolderQuery(caseQryMap);
				if (null != existCase && existCase > 0) {
					message.put("message", "第" + (i + 1) + "行：目录【" + item[2] + "】下已存在同名测试用例【" + item[3] + "】");
					messages.add(message);
					continue;
				}

				CaseFolder testCase = new CaseFolder();
				testCase.setName(item[3]);
				testCase.setParentId(parentId);
				testCase.setProductId(productId);
				testCase.setType("case");
				testCase.setCreator(ParameterThreadLocal.getUserId());
				testingDAO.caseFolderCreate(testCase);

				// 记录新创建的测试用例ID
				int caseId = testCase.getId();
				lastCaseId = caseId;
				if (lastCaseId <= 0) {
					message.put("message", "第" + (i + 1) + "行：测试用例创建失败");
					messages.add(message);
					continue;
				}

				int priority = 2;
				if (item[6].equals("高")) {
					priority = 1;
				} else if (item[6].equals("中")) {
					priority = 2;
				} else if (item[6].equals("低")) {
					priority = 3;
				} else {
					priority = 2;
				}

				CaseInfo caseInfo = new CaseInfo();
				caseInfo.setCaseId(caseId);
				caseInfo.setStatus(1);
				caseInfo.setDesigner(ParameterThreadLocal.getUserId());
				caseInfo.setPriority(priority);
				caseInfo.setTestPeriod(4);
				caseInfo.setTestType(1);
				caseInfo.setProdModule(moduleId);
				caseInfo.setPreCondition(item[5]);
				caseInfo.setSummary(item[4]);
				caseInfo.setTestTypeName("功能测试");
				caseInfo.setPriorityName(item[6]);
				caseInfo.setTestPeriodName("系统测试");
				caseInfo.setStatusName("新建");
				caseInfo.setProdModuleName(item[1]);
				caseInfo.setName(item[3]);

				int infoInsert = testingDAO.caseInfoSave(caseInfo);
				if (infoInsert <= 0) {
					message.put("message", "第" + (i + 1) + "行：测试用例基本信息保存失败");
					messages.add(message);
					continue;
				}
				cases.add(caseInfo);

				CaseStep caseStep = new CaseStep();
				caseStep.setCaseId(caseId);
				try {
					int stepId = Integer.valueOf(item[7]);
					caseStep.setStepId(stepId);
				} catch (NumberFormatException e) {
					message.put("message", "第" + (i + 1) + "行：测试步骤编号为空或者非数字");
					messages.add(message);
					continue;
				}

				caseStep.setOperation(item[8]);
				caseStep.setInputData(item[9]);
				caseStep.setExpectResult(item[10]);
				int stepInsert = testingDAO.caseStepSave(caseStep);

				if (stepInsert <= 0) {
					message.put("message", "第" + (i + 1) + "行：测试步骤信息保存失败");
					messages.add(message);
					continue;
				}
			} else {
				if (lastCaseId <= 0) { //测试用例创建失败
					continue;
				}
				CaseStep caseStep = new CaseStep();
				caseStep.setCaseId(lastCaseId);
				caseStep.setStepId(Integer.valueOf(item[7]));
				caseStep.setOperation(item[8]);
				caseStep.setInputData(item[9]);
				caseStep.setExpectResult(item[10]);
				int stepInsert = testingDAO.caseStepSave(caseStep);

				if (stepInsert <= 0) {
					message.put("message", "第" + (i + 1) + "行：测试步骤信息保存失败");
					messages.add(message);
					continue;
				}
			}
		}

		uploadResult.put("messages", messages);
		uploadResult.put("cases", cases);
		return uploadResult;
	}

	private List<String[]> lineDataHandle(String filePath) {
		CSVFileUtil csvUtil;
		List<String[]> result = new ArrayList<>();

		try {
			URL url = new URL(filePath);
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(1000);
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		try {
			csvUtil = new CSVFileUtil(filePath, "GBK");
			String firstLine = csvUtil.readLine();
			String line;
			int length = firstLine.split(",").length;
			while ((line = csvUtil.readLine()) != null) {
				if (line.indexOf(COMMA) > -1) {
					line = line.replaceAll(COMMA, RMARK);
				}
				String[] lineData = line.split(",");
				if (lineData.length != length) {
					return null;
				} else {
					for (int i = 0; i < lineData.length; i++) {
						lineData[i] = lineData[i].replaceAll(RMARK, COMMA);
					}
					result.add(lineData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int caseFolderCreate(CaseFolder caseFolder) {
		caseFolder.setProductId(ParameterThreadLocal.getProductId());
		testingDAO.caseFolderCreate(caseFolder);

		int caseId = caseFolder.getId();

		if (!caseFolder.getType().equalsIgnoreCase("folder")) {
			CaseInfo caseInfo = new CaseInfo();
			caseInfo.setCaseId(caseId);
			caseInfo.setStatus(1);
			caseInfo.setDesigner(ParameterThreadLocal.getUserId());
			caseInfo.setPriority(2);
			caseInfo.setTestPeriod(4);
			caseInfo.setTestType(1);
			caseInfo.setProdModule(0);
			caseInfo.setRegressMark("N");
			caseInfo.setAutoPath(null);
			caseInfo.setAutoType(null);
			caseInfo.setPreCondition(null);
			caseInfo.setSummary(null);

			testingDAO.caseInfoSave(caseInfo);

			if (caseFolder.getType().equalsIgnoreCase("mind")) {
				CaseMind mind = new CaseMind();
				mind.setCaseId(caseId);
				mind.setMindText("{\"root\":{\"data\":{\"id\":\"0\",\"created\":" + System.currentTimeMillis() + ",\"text\":\""
						+ caseFolder.getName() + "\"},\"children\":[]},\"template\":\"default\",\"theme\":\"fresh-blue\",\"version\":\"1.4.43\"}");
				testingDAO.caseMindSave(mind);
			}

			SEPPHistory history = new SEPPHistory();
			history.setObjId(caseId);
			history.setObjType(1);
			history.setProductId(ParameterThreadLocal.getProductId());
			history.setOperUser(ParameterThreadLocal.getUserId());
			history.setOperType(1);
			history.setNewValue(caseFolder.toString());
			history.setOperComment("创建测试用例【#" + caseFolder.getId() + "】：" + caseFolder.getName());
			history.setObjKey("id");
			historyService.historyInsert(history);
		}
		return caseId;
	}

	@Override
	public int caseFolderDelete(Integer caseId) {
		int productId = ParameterThreadLocal.getProductId();
		int userId = ParameterThreadLocal.getUserId();

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("id", caseId);
		CaseFolder caseNode = testingDAO.treeQuery(dataMap).get(0);

		int mainNode = testingDAO.caseFolderDelete(caseId);

		if (caseNode.getType().equalsIgnoreCase("case") || caseNode.getType().equalsIgnoreCase("mind")) {
			testingDAO.caseStepsDelete(caseId);
			testingDAO.caseInfoDelete(caseId);
			testingDAO.caseMindDelete(caseId);
			testingDAO.caseResultDelete(caseId);

			SEPPHistory history = new SEPPHistory();
			history.setObjId(caseNode.getId());
			history.setObjType(1);
			history.setProductId(productId);
			history.setOperUser(userId);
			history.setReferUser(caseNode.getCreator());
			history.setOperType(3);
			history.setOperComment("测试用例：【#" + caseNode.getId() + " - " + caseNode.getName() + "】被删除");
			history.setObjKey("id");
			historyService.historyInsert(history);
		}

		return mainNode;
	}

	@Override
	public int caseFolderUpdate(CaseFolder caseFolder) {
		return testingDAO.caseFolderUpdate(caseFolder);
	}

	@Override
	public List<Map<String, Object>> caseStepQuery(Integer caseId) {
		return testingDAO.caseStepQuery(caseId);
	}

	@Override
	public int caseStepSave(CaseStep caseStep) {
		int res = testingDAO.caseStepSave(caseStep);

		List<Map<String, Object>> steps = testingDAO.caseStepQuery(caseStep.getCaseId());
		CaseInfo info = testingDAO.caseInfoQuery(caseStep.getCaseId()).get(0);

		if (info.getStatus() - 1 == 0 && steps.size() > 0) {
			testingDAO.caseStatusUpdate(caseStep.getCaseId(), 2);
		}
		return res;
	}

	@Override
	public int caseMindSave(CaseMind caseMind) {
		return testingDAO.caseMindSave(caseMind);
	}

	@Override
	public int caseStepDelete(Integer caseId, Integer stepId) {
		int res = testingDAO.caseStepDelete(caseId, stepId);
		testingDAO.reduceStepIdOnDel(caseId, stepId);
		return res;
	}

	@Override
	public int caseStepUpdate(Map<String, Object> dataMap) {
		return testingDAO.caseStepUpdate(dataMap);
	}

	@Override
	public List<CaseInfo> caseInfoQuery(Integer caseId) {
		return testingDAO.caseInfoQuery(caseId);
	}

	@Override
	public int caseInfoSave(CaseInfo caseInfo) throws IllegalAccessException {
		CaseInfo OldCaseInfo = testingDAO.caseInfoQuery(caseInfo.getCaseId()).get(0);

		Map<String, Object> treeMap = new HashMap<>();
		treeMap.put("id", caseInfo.getCaseId());
		CaseFolder caseNode = testingDAO.treeQuery(treeMap).get(0);

		List<SEPPHistory> histories = new ArrayList<>();
		Class<? extends CaseInfo> cls = caseInfo.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String keyName = field.getName();
			Object newValue = field.get(caseInfo);
			Object oldValue = field.get(OldCaseInfo);

			if (keyName.endsWith("Name")) {
				continue;
			}

			if (!Objects.equals(newValue, oldValue)) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(1);
				history.setObjId(caseNode.getId());
				history.setOperType(2);
				history.setProductId(ParameterThreadLocal.getProductId());
				history.setOperUser(ParameterThreadLocal.getUserId());
				String comment = "测试用例【#" + caseNode.getId() + " - " + caseNode.getName() + "】设计信息修改";
				history.setOperComment(comment);
				history.setReferUser(caseNode.getCreator());
				history.setOrgValue(String.valueOf(oldValue));
				history.setNewValue(String.valueOf(newValue));
				history.setObjKey(keyName);
				histories.add(history);
			}
		}
		if (histories.size() > 0) {
			historyService.historyInsertBatch(histories);
		}

		return testingDAO.caseInfoSave(caseInfo);
	}

	@Override
	public int caseInfoDelete(Integer caseId) {
		return testingDAO.caseInfoDelete(caseId);
	}

	@Override
	public Map<String, Object> caseReadOnlyQuery(Integer caseId) {
		List<Map<String, Object>> relateList = testingDAO.caseRelateInfoQuery(caseId);
		List<Map<String, Object>> readList = testingDAO.caseReadQuery(caseId);
		Map<String, Object> read = new HashMap<>();

		if (readList.size() > 0) {
			read.putAll(readList.get(0));
		}

		for (int i = 0; i < relateList.size(); i++) {
			Map<String, Object> relate = relateList.get(i);
			String key = relate.get("objectType").toString();
			Object value = relate.get("objectNum");
			read.put(key, value);
		}
		return read;
	}

	@Override
	public int caseRelateSave(CaseRelationShip caseRelationShip) {
		Integer userId = ParameterThreadLocal.getUserId();
		Integer relateType = caseRelationShip.getRelateType();
		Integer reqId = caseRelationShip.getReqId();
		String caseIds = caseRelationShip.getCaseIds();

		Map<String, Object> dataMap = new HashMap<>();
		List<Map<String, Object>> toRelate = new ArrayList<>();
		int count;

		if (null == reqId || StringUtils.isEmpty(caseIds)) {
			Integer caseId = caseRelationShip.getCaseId();
			Map<String, Object> treeMap = new HashMap<>();
			treeMap.put("id", caseId);
			List<CaseFolder> caseNode = testingDAO.treeQuery(treeMap);
			if (null == caseNode || caseNode.size() == 0) {
				return 0;
			}

			String[] relates = caseRelationShip.getIds().split(",");
			for (int i = 0; i < relates.length; i++) {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("caseId", caseId);
				tempMap.put("id", Integer.parseInt(relates[i]));
				tempMap.put("relateType", relateType);
				toRelate.add(tempMap);
			}
			dataMap.put("ids", toRelate);

			count = testingDAO.caseRelateSave(dataMap);

			String namedType = relateType == 1 ? "缺陷数据" : "需求数据";
			SEPPHistory history = new SEPPHistory();
			history.setObjType(1);
			history.setObjKey("id");
			history.setObjId(caseId);
			history.setProductId(ParameterThreadLocal.getProductId());
			history.setOperUser(userId);
			history.setOperType(2);
			history.setOperComment("测试用例【#" + caseNode.get(0).getId() + " - " + caseNode.get(0).getName() + "】新关联了" + count + "条" + namedType);
			history.setReferUser(caseNode.get(0).getCreator());
			historyService.historyInsert(history);
		} else {
			Map<String, Object> reqQryMap = new HashMap<>();
			reqQryMap.put("id", reqId);
			List<Requirement> reqs = requirementDAO.reqQuery(reqQryMap);
			if (null == reqs || reqs.size() == 0) {
				return 0;
			}

			String[] relates = caseRelationShip.getCaseIds().split(",");
			for (int i = 0; i < relates.length; i++) {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("caseId", Integer.parseInt(relates[i]));
				tempMap.put("id", reqId);
				tempMap.put("relateType", relateType);
				toRelate.add(tempMap);
			}
			dataMap.put("ids", toRelate);

			count = testingDAO.caseRelateSave(dataMap);
			if (count > 0) {
				SEPPHistory history = new SEPPHistory();
				history.setObjType(2);
				history.setObjKey("id");
				history.setObjId(reqId);
				history.setProductId(ParameterThreadLocal.getProductId());
				history.setOperUser(userId);
				history.setOperType(2);
				history.setOperComment("产品需求【#" + reqs.get(0).getId() + " - " + reqs.get(0).getSummary() + "】新关联了" + count + "条测试用例");
				history.setReferUser(reqs.get(0).getSubmitter());
				historyService.historyInsert(history);
			}
		}

		return count;
	}

	@Override
	public int caseRelateDelete(CaseRelationShip caseRelationShip) {
		Integer caseId = caseRelationShip.getCaseId();
		Integer relateType = caseRelationShip.getRelateType();

		Map<String, Object> treeMap = new HashMap<>();
		treeMap.put("id", caseId);
		CaseFolder caseNode = testingDAO.treeQuery(treeMap).get(0);

		int count = testingDAO.caseRelateDelete(caseRelationShip);

		String namedType = relateType == 1 ? "缺陷关联数据" : "需求关联数据";
		SEPPHistory history = new SEPPHistory();
		history.setObjType(1);
		history.setObjKey("id");
		history.setObjId(caseId);
		history.setProductId(ParameterThreadLocal.getProductId());
		history.setOperUser(ParameterThreadLocal.getUserId());
		history.setOperType(3);
		history.setOperComment("测试用例【#" + caseNode.getId() + " - " + caseNode.getName() + "】删除了" + count + "条" + namedType);
		history.setReferUser(caseNode.getCreator());

		historyService.historyInsert(history);

		return count;
	}

	@Override
	public List<Map<String, Object>> relatedDefectQuery(Integer caseId) {
		return testingDAO.relatedDefectQuery(caseId);
	}

	@Override
	public int caseFolderPaste(CaseFolder caseFolder) {
		final Integer originId = caseFolder.getId();
		caseFolder.setProductId(ParameterThreadLocal.getProductId());
		testingDAO.caseFolderCreate(caseFolder);

		if (!caseFolder.getType().equalsIgnoreCase("folder")) {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("caseId", caseFolder.getId());
			dataMap.put("originId", originId);
			dataMap.put("designer", ParameterThreadLocal.getUserId());
			testingDAO.caseInfoPaste(dataMap);

			if (caseFolder.getType().equalsIgnoreCase("case")) {
				testingDAO.caseStepPaste(caseFolder.getId(), originId);
			} else {
				testingDAO.caseMindPaste(caseFolder.getId(), originId);
			}

			SEPPHistory history = new SEPPHistory();
			history.setObjId(caseFolder.getId());
			history.setObjType(1);
			history.setProductId(ParameterThreadLocal.getProductId());
			history.setOperUser(ParameterThreadLocal.getUserId());
			history.setOperType(1);
			history.setOperComment("创建测试用例【#" + caseFolder.getId() + "】：" + caseFolder.getName());
			history.setObjKey("id");
			historyService.historyInsert(history);
		}
		return caseFolder.getId();
	}

	@Override
	public List<Map<String, Object>> relatedReqQuery(Integer caseId) {
		return testingDAO.relatedReqQuery(caseId);
	}

	@Override
	public String caseMindQuery(Integer caseId) {
		return testingDAO.caseMindQuery(caseId);
	}
}
