package com.pr.sepp.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pr.sepp.env.info.dao.EnvInfoDAO;
import com.pr.sepp.mgr.system.dao.SettingDAO;
import com.pr.sepp.sep.build.dao.BuildInstanceDAO;
import com.pr.sepp.sep.build.service.DeploymentService;
import com.pr.sepp.sep.build.service.impl.BuildHistoryService;
import com.pr.sepp.sep.build.service.trigger.JenkinsStatusUpdateTrigger;
import com.pr.sepp.sep.build.service.trigger.JenkinsStatusUpdater;
import com.pr.sepp.utils.jenkins.ConfigurationJenkinsClientProvider;
import com.pr.sepp.utils.jenkins.DynamicJenkinsClientProvider;
import com.pr.sepp.utils.jenkins.JenkinsClientProvider;
import com.pr.sepp.utils.jenkins.model.JenkinsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JenkinsProperties.class})
public class SeppConfiguration {

	protected final JenkinsProperties jenkinsProperties;

	public SeppConfiguration(JenkinsProperties jenkinsProperties) {
		this.jenkinsProperties = jenkinsProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public JenkinsClientProvider jenkinsClientProvider(JenkinsProperties jenkinsProperties, SettingDAO settingDAO) {
		if (jenkinsProperties.isEnableProgrammatic()) {
			return new ConfigurationJenkinsClientProvider(jenkinsProperties, settingDAO);
		}
		return new DynamicJenkinsClientProvider(jenkinsProperties, settingDAO);
	}

	@Bean
	@ConditionalOnMissingBean
	public JenkinsStatusUpdater jenkinsStatusUpdater(JenkinsClientProvider jenkinsClientProvider,
													 BuildHistoryService buildHistoryService,
													 DeploymentService deploymentService,
													 ObjectMapper mapper,
													 EnvInfoDAO envInfoDAO,
													 BuildInstanceDAO buildInstanceDAO) {

		return new JenkinsStatusUpdater(jenkinsClientProvider, buildHistoryService, deploymentService,
				mapper, envInfoDAO, buildInstanceDAO);
	}

	@Bean
	@ConditionalOnMissingBean
	public JenkinsStatusUpdateTrigger statusUpdateTrigger(JenkinsStatusUpdater jenkinsStatusUpdater,
														  JenkinsProperties jenkinsProperties) {
		return new JenkinsStatusUpdateTrigger(jenkinsStatusUpdater, jenkinsProperties);
	}
}
