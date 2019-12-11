package com.pr.sepp.common.service;

import com.pr.sepp.common.BaseServiceIntegrationTest;
import com.pr.sepp.notify.model.Alert;
import com.pr.sepp.notify.service.AlertService;
import com.github.pagehelper.PageInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.Assert.assertNull;

public class AlertServiceTest extends BaseServiceIntegrationTest {

    @Autowired
    private AlertService alertService;

    @Test
    @Ignore
    @Sql(scripts = "/sql/init-alert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testAlertListPaging() {
        PageInfo<Alert> alertPageInfo = alertService.alertListPaging(9, 1, 10);
        assertNull(alertPageInfo);
    }

}
