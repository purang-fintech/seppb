package com.pr.sepp.utils.rules;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.pr.sepp.common.calculation.helper.WarningRuleHelper;
import com.pr.sepp.sep.defect.model.Defect;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WarningRuleHelperTest {
    private WarningRuleHelper warningRuleHelper;
    private Defect defect;

    @Before
    public void setUp() {
        warningRuleHelper = new WarningRuleHelper();
        defect = new Defect();
        defect.setFixTimes(10);
        defect.setDefectPeriod(20);
        defect.setFoundMeans(30);
        defect.setInfluence(45);
    }


    @Test
    public void execute() {

        boolean fixTimes = warningRuleHelper.execute("fixTimes > 5", defect);
        boolean fixTimesAndFoundMeans = warningRuleHelper.execute("math.abs(fixTimes - foundMeans) > 19", defect);
        boolean period = warningRuleHelper.execute("(foundMeans - fixTimes) / defectPeriod == 1", defect);
        boolean influence = warningRuleHelper.execute("(influence - foundMeans) / defectPeriod > 0 && (influence - foundMeans) / defectPeriod < 1", defect);
        assertTrue(fixTimes);
        assertTrue(fixTimesAndFoundMeans);
        assertTrue(period);
        assertTrue(influence);
    }

    @Test
    public void execute1() {
        Expression compile = AviatorEvaluator.compile("(influence - foundMeans) / defectPeriod > 0 && (influence - foundMeans) / defectPeriod < 1");
        boolean execute = warningRuleHelper.execute(compile, defect);
        assertTrue(execute);
    }
}