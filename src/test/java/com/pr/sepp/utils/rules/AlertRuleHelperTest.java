package com.pr.sepp.utils.rules;

import com.pr.sepp.sep.defect.model.Defect;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AlertRuleHelperTest {
    private AlertRuleHelper alertRuleHelper;
    private Defect defect;

    @Before
    public void setUp() {
        alertRuleHelper = new AlertRuleHelper();
        defect = new Defect();
        defect.setFixTimes(10);
        defect.setDefectPeriod(20);
        defect.setFoundMeans(30);
        defect.setInfluence(45);
    }


    @Test
    public void execute() {

        boolean fixTimes = alertRuleHelper.execute("fix_times > 5", defect);
        boolean fixTimesAndFoundMeans = alertRuleHelper.execute("found_means - fix_times > 19", defect);
        boolean period = alertRuleHelper.execute("(found_means - fix_times) / defect_period == 1", defect);
        boolean influence = alertRuleHelper.execute("(influence - found_means) / defect_period > 0 && (influence - found_means) / defect_period < 1", defect);
        assertTrue(fixTimes);
        assertTrue(fixTimesAndFoundMeans);
        assertTrue(period);
        assertTrue(influence);
    }

    @Test
    public void execute1() {
        Expression compile = AviatorEvaluator.compile("(influence - found_means) / defect_period > 0 && (influence - found_means) / defect_period < 1");
        boolean execute = alertRuleHelper.execute(compile, defect);
        assertTrue(execute);
    }
}