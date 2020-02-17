package com.pr.sepp.common.calculation.helper;

import com.google.common.collect.Maps;
import com.googlecode.aviator.Expression;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

public interface WarningHelper<T> {

    boolean execute(@NonNull String expression, @NonNull T t);

    boolean execute(@NonNull Expression expression, @NonNull T t);

    Object runAndReturn(@NonNull Expression expression, @NonNull T t);

    Object runAndReturn(@NonNull String expression, @NonNull T t);

    default Map<String, Object> beanToMap(@NonNull T t) {
        Map<String, Object> defectMap = Maps.newHashMap();
        BeanMap beanMap = BeanMap.create(t);
        for (Object key : beanMap.keySet()) {
            Object value = valueToDouble(null == beanMap.get(key) ? 0.0001 : beanMap.get(key));
            defectMap.put(String.valueOf(key), value);
        }
        return defectMap;
    }

    static Object valueToDouble(Object value) {
        if (value instanceof Number) {
            value = Double.valueOf(String.valueOf(value));
        }
        return value;
    }
}
