package com.pr.sepp.sep.build.model.resp;

import com.offbytwo.jenkins.model.BuildResult;
import com.pr.sepp.sep.build.model.constants.JenkinsBuildStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JenkinsBuildResp {
	private Integer buildVersion;
	private boolean showSelect = false;
	private JenkinsBuildStatus status;


	public static JenkinsBuildResp apply(Integer number, BuildResult result) {
		JenkinsBuildResp jenkinsBuildResp = new JenkinsBuildResp();
		jenkinsBuildResp.setBuildVersion(number);
		jenkinsBuildResp.setShowSelect(false);
		jenkinsBuildResp.setStatus(Objects.equals(result, null) ? JenkinsBuildStatus.BUILDING :
				JenkinsBuildStatus.valueOf(result.name()));
		return jenkinsBuildResp;
	}

	public boolean canDeploy() {
		return this.getStatus() == JenkinsBuildStatus.UNSTABLE
				|| this.getStatus() == JenkinsBuildStatus.SUCCESS;
	}
}
