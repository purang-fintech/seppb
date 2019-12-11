package com.pr.sepp.file.controller;

import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.file.model.SEPPFile;
import com.pr.sepp.file.model.TestMail;
import com.pr.sepp.file.service.FileService;
import com.pr.sepp.utils.fasfdfs.FdfsProperties;
import com.pr.sepp.utils.fasfdfs.server.FastDFSClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@ResponseBody
public class FileController {
	@Resource
	private FileService fileService;

	@Autowired
	private FastDFSClient client;

	@Autowired
	private FdfsProperties properties;

	private String getWholeName(String fileName) {
		int index = fileName.lastIndexOf(".");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String pref = fileName.substring(0, index) + " @ " + formatter.format(new Date());
		return pref + fileName.substring(index);
	}

	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	public List<Map<String, Object>> commonUpload(@RequestParam(value = "fileName") String fileName, @RequestParam(value = "file") MultipartFile[] file) {
		List<Map<String, Object>> result = new ArrayList<>();

		for (int i = 0; i < file.length; i++) {
			StringBuffer subPath = new StringBuffer();
			subPath.append(properties.getServer());

			Map<String, Object> data = new HashMap<>();
			SEPPFile fileModel = new SEPPFile();

			String filePath = client.uploadFileWithMultipart(file[i]);
			subPath.append(filePath);
			fileModel.setFileName(fileName);
			fileModel.setUrl(subPath.toString());
			fileModel.setUploadUser(ParameterThreadLocal.getUserId());
			fileService.attachCreate(fileModel);
			data.put("id", fileModel.getId());
			data.put("name", fileName);
			data.put("url", subPath);
			result.add(data);
		}
		return result;
	}


	@RequestMapping(value = "/vuemde/img_upload", method = RequestMethod.POST)
	public Map<String, Object> defectImgUpload(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file) {
		Map<String, Object> result = new HashMap<>();
		StringBuffer subPath = new StringBuffer();
		subPath.append(properties.getServer());
		SEPPFile fileModel = new SEPPFile();
		String oldName = file.getOriginalFilename();
		String fileName = getWholeName(oldName);
		String filePath = client.uploadFileWithMultipart(file);
		String fileUrl = subPath.append(filePath).toString();
		fileModel.setFileName(fileName);
		fileModel.setUrl(fileUrl);
		fileModel.setUploadUser(ParameterThreadLocal.getUserId());
		fileService.attachCreate(fileModel);
		result.put("name", fileModel.getFileName());
		result.put("id", fileModel.getId());
		result.put("url", fileUrl);
		return result;
	}

	@RequestMapping(value = "/file/base64img_upload", method = RequestMethod.POST)
	public Map<String, Object> base64ImgUpload(HttpServletRequest request) throws Exception {
		Map<String, Object> result = new HashMap<>();
		StringBuffer subPath = new StringBuffer();
		subPath.append(properties.getServer());
		MultipartFile multipartFile;
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			multipartFile = multipartRequest.getFile("file");
		} else {
			return null;
		}
		String filePath = client.uploadFileWithMultipart(multipartFile);
		String fileUrl = subPath.append(filePath).toString();
		String fileName = UUID.randomUUID().toString() + ".png";
		SEPPFile fileModel = new SEPPFile();
		fileModel.setFileName(fileName);
		fileModel.setUrl(fileUrl);
		fileModel.setUploadUser(ParameterThreadLocal.getUserId());
		fileService.attachCreate(fileModel);
		result.put("id", fileModel.getId());
		result.put("url", fileUrl);
		return result;
	}

	@RequestMapping(value = "/file/delete/{attachIds}", method = RequestMethod.POST)
	public int fileDelete(@PathVariable("attachIds") String id) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("id", id);
		List<SEPPFile> files = fileService.attachQuery(dataMap);
		SEPPFile toDel = files.get(0);
		String url = toDel.getUrl().substring(toDel.getUrl().indexOf(properties.getGroupName()));
		// 先逻辑删除，后物理删除
		Map<String, String> delMap = new HashMap<>();
		delMap.put("id", id);
		int result = fileService.attachDelete(delMap);
		client.deleteFile(url);

		return result;
	}

	@RequestMapping(value = "/file/query/{fileIds}", method = RequestMethod.POST)
	public List<SEPPFile> attachQuery(@PathVariable("fileIds") String fileIds) {
		Map<String, Object> dataMap = new HashMap<>();
		if (!StringUtils.isEmpty(fileIds)) {
			dataMap.put("files", Arrays.asList(fileIds.split(",")));
		}
		return fileService.attachQuery(dataMap);
	}

	@ResponseBody
	@RequestMapping(value = "/file/download", method = RequestMethod.POST)
	public byte[] fileDownload(@RequestParam(value = "file") String file) {
		return fileService.fileDownload(file, properties.getGroupName());
	}

	@PostMapping("/file/attach/email::send")
	public void attachSendEmail(@RequestBody TestMail testMail) {
		fileService.sendAttachMail(testMail);
	}

}
