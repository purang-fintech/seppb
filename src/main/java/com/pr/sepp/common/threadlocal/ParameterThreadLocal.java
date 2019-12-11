package com.pr.sepp.common.threadlocal;

import static org.apache.commons.lang.StringUtils.EMPTY;

public class ParameterThreadLocal {

    public static ThreadLocal<Parameter> parameterThreadLocal = new ThreadLocal<>();

    public static void setUserId(Integer uid) {
        Parameter parameter = getParameter();
        parameter.setUserId(uid);
    }

    public static Integer getUserId() {
        Parameter parameter = parameterThreadLocal.get();
        if (parameter != null) {
            return parameter.getUserId();
        }
        return Integer.MAX_VALUE;
    }

    public static void setProductId(Integer productId) {
        Parameter parameter = getParameter();
        parameter.setProductId(productId);
    }

    public static Integer getProductId() {
        Parameter parameter = parameterThreadLocal.get();
        if (parameter != null) {
            return parameter.getProductId();
        }
        return Integer.MAX_VALUE;
    }

    public static void setPageSize(Integer pageSize) {
        Parameter parameter = getParameter();
        parameter.setPageSize(pageSize);
    }

    public static Integer getPageSize() {
        Parameter parameter = parameterThreadLocal.get();
        if (parameter != null) {
            return parameter.getPageSize();
        }
        return 500;
    }

    public static void setPageNum(Integer pageNum) {
        Parameter parameter = getParameter();
        parameter.setPageNum(pageNum);
    }

    public static Integer getPageNum() {
        Parameter parameter = parameterThreadLocal.get();
        if (parameter != null) {
            return parameter.getPageNum();
        }
        return 1;
    }

    public static String getHttpId() {
        Parameter parameter = parameterThreadLocal.get();
        if (parameter != null) {
            return parameter.getHttpId();
        }
        return EMPTY;
    }

    public static void setHttpId(String httpId) {
        Parameter parameter = getParameter();
        parameter.setHttpId(httpId);
    }

    private static Parameter getParameter() {
        Parameter parameter = parameterThreadLocal.get();
        if (parameter == null) {
            parameter = Parameter.builder().build();
            parameterThreadLocal.set(parameter);
        }
        return parameter;
    }

    public static void clear() {
        parameterThreadLocal.remove();
    }

}
