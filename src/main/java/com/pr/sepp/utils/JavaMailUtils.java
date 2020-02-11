package com.pr.sepp.utils;

import com.pr.sepp.common.config.TaskSchedulerConfig;
import com.pr.sepp.common.config.factory.CommonFactory;
import com.pr.sepp.common.constants.MailDTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static com.google.common.base.Verify.verifyNotNull;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public final class JavaMailUtils {
    private static final String WARNING_RETRY_ERROR = "邮件发送给：%s失败,详情见错误日志";

    private JavaMailUtils() {
    }

    /**
     * 最基础的邮件发送接口（包含：标题、内容）
     *
     * @param mailDTO
     * @throws MessagingException
     */
    public static boolean sendMail(@NonNull final MailDTO mailDTO) throws InterruptedException, ExecutionException,MessagingException {
        ApplicationContext appCtx = CommonFactory.getApplicationContext();
        JavaMailSender javaMailSender = appCtx.getBean(JavaMailSender.class);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        buildMimeMessage(mailDTO, mimeMessage);
        if (mailDTO.isFailRetry()) {
            return failRetrySendMail(mailDTO, javaMailSender, mimeMessage);
        }
        return sendMail(javaMailSender, mimeMessage);
    }

    /**
     * @param mailDTO
     * @param javaMailSender
     * @param mimeMessage
     * @return
     * @throws Exception
     */
    private static boolean failRetrySendMail(@NonNull MailDTO mailDTO, JavaMailSender javaMailSender, MimeMessage mimeMessage)
            throws InterruptedException, ExecutionException {
        if (mailDTO.isBlock()) {
            return asynchronousMailFailRetry(mailDTO, javaMailSender, mimeMessage);

        } else {
            synchronizedMailFailRetry(mailDTO, javaMailSender, mimeMessage);
            return false;
        }
    }

    /**
     * 同步发送邮件(不包含重试机制)
     */
    public static boolean sendMail(JavaMailSender javaMailSender, MimeMessage message) {
        javaMailSender.send(message);
        return true;
    }

    /**
     * 同步重试机制（调用该接口后，当前线程处于同步状态，会发生阻塞，但是能拿到发送邮件的结果：true 成功；false 失败）
     * {@link ThreadPoolExecutor}
     *
     * @param mailDTO
     * @param javaMailSender
     * @param mimeMessage
     */
    public static Boolean asynchronousMailFailRetry(MailDTO mailDTO, JavaMailSender javaMailSender, MimeMessage mimeMessage)
            throws InterruptedException, ExecutionException {
        ThreadPoolExecutor executor = TaskSchedulerConfig.getExecutor();
        return executor.submit(() -> new FailMailRetryThread(mailDTO, javaMailSender, mimeMessage).call()).get();
    }

    /**
     * 异步重试机制（调用该接口后，在邮件发送失败后会失败重试，但是无法在主程序中获取该接口的执行结果）
     * {@link ThreadPoolExecutor}
     *
     * @param mailDTO
     * @param javaMailSender
     * @param mimeMessage
     * @throws Exception
     */
    public static void synchronizedMailFailRetry(MailDTO mailDTO, JavaMailSender javaMailSender, MimeMessage mimeMessage) {
        ThreadPoolExecutor executor = TaskSchedulerConfig.getExecutor();
        executor.execute(() -> new FailMailRetryThread(mailDTO, javaMailSender, mimeMessage).call());
    }

    private static void buildMimeMessage(MailDTO mailDTO, MimeMessage mimeMessage) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(verifyNotNull(mailDTO.getFrom(), "邮件发送人不能为空"));
        helper.setTo(verifyNotNull(mailDTO.getTo(), "邮件接收人不能为空"));
        helper.setText(mailDTO.getContent(), mailDTO.isHtml());
        helper.setSubject(mailDTO.getSubject());
        if (!isEmpty(mailDTO.getTocc()) && isNotBlank(mailDTO.getTocc()[0])) {
            helper.setCc(mailDTO.getTocc());
        }
        if (mailDTO.isHasImage()) {
            helper.addInline(mailDTO.getImageId(), mailDTO.ResourceImage());
        }
        if (mailDTO.isHasAttachment()) {
            FileSystemResource docx = new FileSystemResource(new File(mailDTO.getAttachmentPath()));
            helper.addAttachment(mailDTO.getAttachmentName(), docx);
        }
    }

    static class FailMailRetryThread {
        private MailDTO mailDTO;
        private JavaMailSender javaMailSender;
        private MimeMessage mimeMessage;

        FailMailRetryThread(MailDTO mailDTO, JavaMailSender javaMailSender, MimeMessage mimeMessage) {
            this.mailDTO = mailDTO;
            this.javaMailSender = javaMailSender;
            this.mimeMessage = mimeMessage;
        }

        public Boolean call() {
            int times = 0;
            boolean isSuccess = false;
            while (times <= mailDTO.getFailRetryCount()) {
                try {
                    sendMail(javaMailSender, mimeMessage);
                    isSuccess = true;
                    break;
                } catch (Exception e) {
                    log.error("邮件发送给{}失败{}", mailDTO.getTo(), e);
                    try {
                        if (times >= mailDTO.getFailRetryCount()) {
                            MimeMessage mimeMessageNew = javaMailSender.createMimeMessage();
                            MimeMessageHelper helperRetry = new MimeMessageHelper(mimeMessageNew, true);
                            helperRetry.setFrom(mailDTO.getFrom());
                            helperRetry.setTo(mailDTO.getFailTo());
                            helperRetry.setSubject(mailDTO.getFailSubject());
                            String[] to = mailDTO.getTo();
                            helperRetry.setText(String.format(WARNING_RETRY_ERROR, to), false);
                            sendMail(javaMailSender, mimeMessageNew);
                        }
                        times++;
                        Thread.sleep(mailDTO.getFailRetryTime());
                    } catch (Exception ex) {
                        times++;
                        log.error("邮件重试发送失败{}", e);
                    }
                }
            }
            return isSuccess;
        }
    }
}
