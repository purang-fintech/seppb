package com.pr.sepp.common.schedule;

import com.pr.sepp.common.constants.Env;
import com.pr.sepp.common.schedule.condition.ConditionalOnEnvironment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "spring.schedule.endpoint", value = "enabled", havingValue = "true", matchIfMissing = false)
@Configuration
@ConditionalOnEnvironment(values = {Env.DEV, Env.LOCAL})
public class ScheduleEndpointConfiguration {
    /**
     * 注册 ScheduleMethodHandlerAdapter Bean
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ScheduleMethodHandlerAdapter scheduleMethodHandlerAdapter() {
        return new ScheduleMethodHandlerAdapter();
    }

    /**
     * 注册ScheduleMethodHandlerMapping Bean
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ScheduleMethodHandlerMapping scheduleMethodHandlerMapping() {
        return new ScheduleMethodHandlerMapping();
    }
}