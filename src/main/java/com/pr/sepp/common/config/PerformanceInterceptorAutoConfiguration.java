package com.pr.sepp.common.config;

import com.pr.sepp.common.log.mysql.PerformanceInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;


@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@ConditionalOnProperty(name = "performance.enabled", havingValue = "true")
@EnableConfigurationProperties(PerformanceProperties.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class PerformanceInterceptorAutoConfiguration {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;

    @Autowired
    private PerformanceProperties properties;

    @PostConstruct
    public void addPageInterceptor() {
        PerformanceInterceptor interceptor = new PerformanceInterceptor();
        Properties properties = new Properties();
        properties.putAll(this.properties.getProperties());
        interceptor.setProperties(properties);
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
        }
    }

}
