package com.pr.sepp.common.constants;

import lombok.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.net.URL;

import static com.pr.sepp.common.constants.MailDTO.Address.LOCAL;
import static com.pr.sepp.common.constants.MailDTO.Address.NET;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailDTO {

    @NonNull
    private String from;

    /**
     * 邮件基础设置
     */
    //发送人
    @NonNull
    private String[] to;
    //抄送人
    private String[] tocc;
    //邮件内容
    @NonNull
    private String content;
    //邮件标题
    @NonNull
    private String subject;

    //发送是否为html格式
    private boolean isHtml;


    /**
     * 失败重试
     */
    //发送失败是否重试
    private boolean failRetry;
    //失败重试次数
    private int failRetryCount;
    //失败重试间隔时间
    private long failRetryTime;
    //邮件失败通知人
    private String[] failTo;
    //失败通知标题
    private String failSubject;
    //true :表示同步；false：表示异步
    private boolean isBlock;

    /**
     * 包含图片
     */
    private boolean hasImage;
    //图片路径
    private String imagePath;
    private String imageId;
    //默认读取本地的图片
    @Builder.Default
    private Address imageAddress = LOCAL;

    /**
     * 附件
     */
    private boolean hasAttachment;
    private String attachmentPath;
    private String attachmentName;


    public Resource ResourceImage() {
        if (imageAddress == LOCAL) {
            return localResourceImage(imagePath);
        }
        if (imageAddress == NET) {
            return internetResourceImage(imagePath);
        }
        return null;
    }


    /**
     * 图片本地连接地址
     *
     * @return
     */
    public Resource localResourceImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            return new FileSystemResource(imageFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 图片网络地址
     *
     * @param imagePath
     * @return
     */
    public Resource internetResourceImage(String imagePath) {
        try {
            URL url = new URL(imagePath);
            return new FileSystemResource(ResourceUtils.getFile(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum Address {
        LOCAL,
        NET
    }
}