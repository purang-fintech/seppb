package com.pr.sepp.utils.jenkins;

import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.sep.build.model.constants.InstanceType;
import com.pr.sepp.utils.jenkins.model.JenkinsProperties;

/**
 * 该类仅在本地开发时会读取配置文件中的jenkins的信息
 * 可以将jenkins.enable-programmatic配置置为true，并且加上如下配置：
 * <p>
 * jenkins.web-username=sepp
 * jenkins.web-password=11c66ae929bd59799d4038c12013fdbedf
 * jenkins.web-url=http://ci.sepp.com/
 * <p>
 * jenkins.ios-username=sepp
 * jenkins.ios-password=11cf56067fd6ce9c7027e2ec5378c16d5b
 * jenkins.ios-url=http://android-ci.sepp.com/
 * <p>
 * jenkins.android-username=sepp
 * jenkins.android-password=115718be7165ab3b57448ad6ddc29ab373
 * jenkins.android-url=http://ios-ci.sepp.com/
 */

public class ConfigurationJenkinsClientProvider extends JenkinsClientProvider {

	public ConfigurationJenkinsClientProvider(JenkinsProperties jenkinsProperties, SettingDAO settingDAO) {
		super(jenkinsProperties, settingDAO);
	}

	@Override
	protected boolean shouldUpdateJenkinsClient() {
		return false;
	}

	@Override
	protected void retrieveJenkinsClient() {
		if (jenkinsProperties.androidJenkinsNonNull()) {
			JenkinsClient androidJenkinsClient = createJenkinsClient(jenkinsProperties.getAndroidUrl(),
					jenkinsProperties.getAndroidUsername(), jenkinsProperties.getAndroidPassword());
			JenkinsClient buildAndroidClient = createJenkinsClient(jenkinsProperties.getAndroidUrl(),
					jenkinsProperties.getAndroidUsername(), jenkinsProperties.getAndroidPassword());
			jenkinsClientProviderMap.put(InstanceType.ANDROID.getBeanName(), androidJenkinsClient);
			buildJenkinsClientProviderMap.put(InstanceType.ANDROID.getBeanName(), buildAndroidClient);
		}

		if (jenkinsProperties.iosJenkinsNonNull()) {
			JenkinsClient iosJenkinsClient = createJenkinsClient(jenkinsProperties.getIosUrl(),
					jenkinsProperties.getIosUsername(), jenkinsProperties.getIosPassword());
			JenkinsClient buildIosClient = createJenkinsClient(jenkinsProperties.getIosUrl(),
					jenkinsProperties.getIosUsername(), jenkinsProperties.getIosPassword());

			jenkinsClientProviderMap.put(InstanceType.IOS.getBeanName(), iosJenkinsClient);
			buildJenkinsClientProviderMap.put(InstanceType.IOS.getBeanName(), buildIosClient);
		}

		if (jenkinsProperties.webJenkinsNonNull()) {
			JenkinsClient webJenkinsClient = createJenkinsClient(jenkinsProperties.getWebUrl(),
					jenkinsProperties.getWebUsername(), jenkinsProperties.getWebPassword());
			JenkinsClient buildWebClient = createJenkinsClient(jenkinsProperties.getWebUrl(),
					jenkinsProperties.getWebUsername(), jenkinsProperties.getWebPassword());
			jenkinsClientProviderMap.put(InstanceType.ORDINARY.getBeanName(), webJenkinsClient);
			buildJenkinsClientProviderMap.put(InstanceType.ORDINARY.getBeanName(), buildWebClient);
		}
	}
}
