package com.pr.sepp.common.quartz;

import org.apache.commons.lang.StringUtils;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzConfiguration {

	@Autowired
	private JobFactory jobFactory;

	@Value("${jdbc.url}")
	private String jdbcUrl;

	@Value("${jdbc.username}")
	private String jdbcUserName;

	@Value("${jdbc.password}")
	private String jdbcPassword;

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setJobFactory(jobFactory);
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setStartupDelay(1);
		schedulerFactoryBean.setQuartzProperties(quartzProperties());
		return schedulerFactoryBean;
	}

	@Bean
	public Scheduler scheduler() throws IOException {
		return schedulerFactoryBean().getScheduler();
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		Properties result = propertiesFactoryBean.getObject();

		// 从环境信息中剥离出配置好的环境信息，覆盖默认的配置
		String dsName = result.getProperty("org.quartz.jobStore.dataSource");
		if (StringUtils.isEmpty(dsName)) {
			throw new RuntimeException("quartz配置文件错误，org.quartz.jobStore.dataSource不能为空！");
		}
		result.setProperty("org.quartz.dataSource." + dsName + ".URL", jdbcUrl);
		result.setProperty("org.quartz.dataSource." + dsName + ".user", jdbcUserName);
		result.setProperty("org.quartz.dataSource." + dsName + ".password", jdbcPassword);
		return result;
	}
}
