package com.pr.sepp.common.condition;

import com.pr.sepp.common.BaseIntegrationTest;
import com.pr.sepp.common.constants.Env;
import com.pr.sepp.common.schedule.condition.ConditionalOnEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ConditionOnEnvironmentTest.TestConfiguration.class)
public class ConditionOnEnvironmentTest extends BaseIntegrationTest {

    static boolean LOCAL = false;
    static boolean DEV = false;
    static boolean TEST = false;
    static boolean PROD = false;
    static boolean UNKNOWN = false;

    @Test
    public void test() throws Exception {
        assertTrue(LOCAL);
        assertFalse(DEV);
        assertFalse(TEST);
        assertFalse(PROD);
    }

    @Configuration
    static class TestConfiguration {

        @Configuration
        @ConditionalOnEnvironment(values = Env.LOCAL)
        static class SomeConfiguration {
            {
                LOCAL = true;
            }
        }

        @Configuration
        @ConditionalOnEnvironment(values = Env.DEV)
        static class AnotherConfiguration {
            {
                DEV = true;
            }
        }


        @Configuration
        @ConditionalOnEnvironment(values = Env.TEST)
        static class YetAnotherConfiguration {
            {
                TEST = true;
            }
        }

        @Configuration
        @ConditionalOnEnvironment(values = Env.PROD)
        static class CombinedConfiguration {
            {
                PROD = true;
            }
        }

        @Configuration
        @ConditionalOnEnvironment(values = {Env.LOCAL, Env.DEV})
        static class AnotherCombinedConfiguration {
            {
//                DEV = true;
//                LOCAL = true;
            }
        }

    }

}
