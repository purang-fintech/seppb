package com.pr.sepp.utils.rules;

import com.google.common.collect.Maps;
import com.googlecode.aviator.Expression;
import lombok.NonNull;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

public interface AlertHelper<T> {

    boolean execute(@NonNull String expression, @NonNull T t);

    boolean execute(@NonNull Expression expression, @NonNull T t);

    Object runAndReturn(@NonNull Expression expression, @NonNull T t);

    Object runAndReturn(@NonNull String expression, @NonNull T t);

    default Map<String, Object> beanToMap(@NonNull T t) {
        Map<String, Object> defectMap = Maps.newHashMap();
        BeanMap beanMap = BeanMap.create(t);
        for (Object key : beanMap.keySet()) {
            Object value = valueToDouble(beanMap.get(key));
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
