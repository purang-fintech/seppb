package com.pr.sepp.sep.build.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pr.sepp.sep.build.model.req.BuildHistoryReq;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildFile {
	private Integer id;
	private Integer noteId;
	private String instance;
	private String paramKey;
	private String paramValue;
	private Integer instances;
	private List<BuildParam> params;


	public static Map<String, String> buildFilesToParamsMap(List<BuildFile> buildFiles) {
		if (CollectionUtils.isEmpty(buildFiles)) {
			return Maps.newHashMap();
		}
		return buildFiles.stream().collect(toMap(BuildFile::getParamKey, buildFile -> {
			if (StringUtils.isNotBlank(buildFile.getParamValue())) {
				return buildFile.getParamValue().replace("\n", " ");
			}
			return buildFile.getParamValue();
		}));
	}

	public static List<BuildFile> newParamBuildFiles(BuildFile file, BuildFile... buildFiles) {
		List<BuildFile> files = Lists.newArrayList();
		for (BuildFile buildFile : buildFiles) {
			buildFile.setNoteId(file.getNoteId());
			buildFile.setInstance(file.getInstance());
			files.add(buildFile);
		}
		return files;
	}

	public static BuildFile of(String paramKey, String paramValue) {
		BuildFile newBuildFile = new BuildFile();
		newBuildFile.setParamKey(paramKey);
		newBuildFile.setParamValue(paramValue);
		return newBuildFile;
	}

	/**
	 * 创建job构建时需要的默认参数
	 *
	 * @param buildHistoryReq
	 * @param file
	 * @return
	 */
	public static List<BuildFile> createDefaultBuildFiles(BuildHistoryReq buildHistoryReq, BuildFile file) {
		//封装job所需的默认参数
		return newParamBuildFiles(file,
				of("gitVersion", buildHistoryReq.getGitVersion()),
				of("sonarScan", buildHistoryReq.getGitVersion()),
				of("buildType", buildHistoryReq.getBuildType().name()));
	}
}
