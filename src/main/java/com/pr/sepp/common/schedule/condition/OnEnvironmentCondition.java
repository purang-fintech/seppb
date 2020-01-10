package com.pr.sepp.common.schedule.condition;

import com.google.common.collect.Sets;
import com.pr.sepp.common.constants.Env;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

public class OnEnvironmentCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnEnvironment.class.getName());
        Env[] envs = (Env[]) attributes.get("values");
        Env currentEnv = Env.getCurrentEnv();
        return Sets.newHashSet(envs).contains(currentEnv);
    }
}
