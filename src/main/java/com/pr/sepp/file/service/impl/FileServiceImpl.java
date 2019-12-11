package com.pr.sepp.file.service.impl;

import java.util.List;
import java.util.Map;

import com.pr.sepp.common.constants.MailDTO;
import com.pr.sepp.common.exception.SeppServerException;
import com.pr.sepp.common.threadlocal.ParameterThreadLocal;
import com.pr.sepp.file.model.TestMail;
import com.pr.sepp.mgr.user.dao.UserDAO;
import com.pr.sepp.mgr.user.model.User;
import com.pr.sepp.utils.JavaMailUtils;
import com.pr.sepp.utils.fasfdfs.server.FastDFSClient;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pr.sepp.file.dao.FileDAO;
import com.pr.sepp.file.model.SEPPFile;
import com.pr.sepp.file.service.FileService;

import org.springframework.mail.MailSendException;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private static final String EMAIL_FOOTER = "<span style='float:right'><h4>from:&nbsp;%s<br>email:&nbsp;%s<br><br><br><br></h4></span>";

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FastDFSClient client;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public int attachCreate(SEPPFile file) {
        return fileDAO.attachCreate(file);
    }

    @Override
    public int attachDelete(Map<String, String> dataMap) {
        return fileDAO.attachDelete(dataMap);
    }

    @Override
    public byte[] fileDownload(String fileObject, String groupName){
        JSONObject json = JSONObject.fromObject(fileObject);
        String remoteUrl = json.get("url").toString();
        remoteUrl = remoteUrl.substring(remoteUrl.indexOf(groupName));
        return client.download(remoteUrl);
    }

    @Override
    public List<SEPPFile> attachQuery(Map<String, Object> dataMap) {
        return fileDAO.attachQuery(dataMap);
    }

    @Override
    public void sendAttachMail(TestMail testMail) {
        Integer userId = Integer.valueOf(ParameterThreadLocal.getUserId());
        User user = userDAO.findUserByUserId(userId);
        String body = testMail.getBody().concat(String.format(EMAIL_FOOTER, user.getUserName(), user.getUserEmail()));
        MailDTO mailDTO;
        if (!isEmpty(testMail.getCc())) {
            mailDTO = MailDTO.builder().from(fromEmail)
                    .to(testMail.getTo())
                    .tocc(testMail.getCc())
                    .content(body)
                    .isHtml(true)
                    .subject(testMail.getSubject())
                    .build();
        } else {
            mailDTO = MailDTO.builder().from(fromEmail)
                    .to(testMail.getTo())
                    .content(body)
                    .isHtml(true)
                    .subject(testMail.getSubject())
                    .build();
        }
        try {
            JavaMailUtils.sendMail(mailDTO);
        } catch (MailSendException ne) {
            if (ne.getMessage().indexOf("550 User not found") > 0) {
                log.error("邮件地址不存在", ne.getMessage());
            } else {
                throw new SeppServerException("邮件发送失败,请重试", ne);
            }
        } catch (Exception e) {
            throw new SeppServerException("邮件发送失败,请重试", e);
        }
    }

}
