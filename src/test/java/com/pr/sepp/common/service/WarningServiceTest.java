package com.pr.sepp.common.service;

import com.pr.sepp.common.BaseServiceIntegrationTest;
import com.pr.sepp.notify.warning.service.WarningService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.Assert.assertNull;

public class WarningServiceTest extends BaseServiceIntegrationTest {

    @Autowired
    private WarningService warningService;

    @Test
    @Ignore
    @Sql(scripts = "/sql/init-warning.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testWarningListPaging() {
        assertNull("warningPageInfo");
    }

}
