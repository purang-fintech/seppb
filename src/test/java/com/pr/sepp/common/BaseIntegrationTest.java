package com.pr.sepp.common;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class BaseIntegrationTest {

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("env", "local");
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("env");
    }
}
