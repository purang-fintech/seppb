package com.pr.sepp.common;

import com.pr.sepp.SeppApplication;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = SeppApplication.class
)
public abstract class BaseServiceIntegrationTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("env", "local");
    }

    @AfterClass
    public static void afterClass() {
        System.clearProperty("env");
    }
}
