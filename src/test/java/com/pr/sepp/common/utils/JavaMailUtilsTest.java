package com.pr.sepp.common.utils;

import com.pr.sepp.common.config.factory.CommonFactory;
import com.pr.sepp.common.constants.MailDTO;
import com.pr.sepp.utils.JavaMailUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.Mockito.*;

@PrepareForTest(CommonFactory.class)
@RunWith(PowerMockRunner.class)
public class JavaMailUtilsTest {

    @Mock
    private ApplicationContext ac;
    private MailDTO mailDTO = new MailDTO();

    @Before
    public void before() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setPort(25);
        javaMailSender.setHost("mail.purang.com");
        javaMailSender.setPassword("Purang123");
        javaMailSender.setUsername("autotest@purang.com");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", String.valueOf(true));
        properties.setProperty("mail.smtp.starttls.enable", String.valueOf(true));
        properties.setProperty("mail.smtp.starttls.required", String.valueOf(true));
        mailDTO.setTo(new String[]{"chenlang@purang.com"});
        mailDTO.setSubject("test");
        mailDTO.setHtml(false);
        mailDTO.setFailRetry(true);
        mailDTO.setContent("test mail");
        mailDTO.setFailTo(new String[]{"chenlang@purang"});
        mailDTO.setFailSubject("failed mail");
        mailDTO.setFailRetryTime(10 * 1000);
        mailDTO.setFailRetryCount(3);
        mailDTO.setFrom("chenlang@purang.com");
        PowerMockito.mockStatic(CommonFactory.class);
        PowerMockito.when(CommonFactory.getApplicationContext()).thenReturn(ac);
        javaMailSender.setJavaMailProperties(properties);
        when(ac.getBean(JavaMailSender.class)).thenReturn(javaMailSender);

    }


    @Ignore
    @Test
    public void buildMimeMessageTest() throws Exception {
        MailDTO mailDTO = mock(MailDTO.class);
        mailDTO.setHasImage(true);
        mailDTO.setImageAddress(MailDTO.Address.LOCAL);
        mailDTO.setImagePath(EMPTY);
        mailDTO.ResourceImage();
        verify(mailDTO, times(1)).ResourceImage();

    }

    @Ignore
    @Test
    public void asynchronousSendMailTest() throws Exception {
        mailDTO.setBlock(true);
        boolean b = JavaMailUtils.sendMail(mailDTO);
        Assert.assertFalse(b);


    }

    @Ignore
    @Test
    public void synchronizedSendMailTest() throws Exception {
        mailDTO.setBlock(false);
        boolean b = JavaMailUtils.sendMail(mailDTO);
        Assert.assertFalse(b);
    }
}
