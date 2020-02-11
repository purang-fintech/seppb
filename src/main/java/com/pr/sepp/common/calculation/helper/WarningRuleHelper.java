package com.pr.sepp.common.calculation.helper;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.springframework.stereotype.Service;

@Service
public class WarningRuleHelper<T> implements WarningHelper<T> {
    @Override
    public boolean execute(String expression, T t) {
        return (boolean) runAndReturn(expression, t);
    }

    @Override
    public boolean execute(Expression expression, T t) {
        return (boolean) runAndReturn(expression, t);
    }

    @Override
    public Object runAndReturn(String expression, T t) {
        return AviatorEvaluator.execute(expression, beanToMap(t));
    }

    @Override
    public Object runAndReturn(Expression expression, T t) {
        return expression.execute(beanToMap(t));
    }
}
