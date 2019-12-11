package com.pr.sepp.auth.core.permission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPermission {

	boolean dynamic() default true;

	RoleGroup[] groups() default RoleGroup.ALL;

	enum RoleGroup {
		ALL
	}

}
