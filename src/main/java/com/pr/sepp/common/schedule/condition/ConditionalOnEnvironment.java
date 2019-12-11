package com.pr.sepp.common.schedule.condition;


import com.pr.sepp.common.constants.Env;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnEnvironmentCondition.class)
public @interface ConditionalOnEnvironment {

    Env[] values() default {Env.LOCAL};
}
