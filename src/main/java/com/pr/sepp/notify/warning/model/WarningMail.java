package com.pr.sepp.notify.warning.model;

import lombok.Data;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;

import java.io.IOException;

@Data
public class WarningMail {
    private Long id;
    private Integer batchId;
    private Long batchNo;
    private Integer warningId;
    private Integer productId;
    private String productName;
    private Integer to;
    private String toEmail;
    private String toName;
    private boolean isSent;
    private Integer sendGateway;

    private Integer type;
    private String typeName;
    private Integer subType;
    private String subTypeName;
    private Integer level;
    private String levelName;
    private String warningDate;
    private String category;
    private String summary;
    private String content;

    public static HtmlCanvas buildTableHeader(HtmlCanvas htmlCanvas) throws IOException {
        HtmlAttributes attributes = new HtmlAttributes();
        attributes.add("onmouseover","this.style.backgroundColor='#ffff66';");
        attributes.add("onmouseout","this.style.backgroundColor='#d4e3e5';");
        return htmlCanvas.tr(attributes)
                .th().content("产品/项目名称")
                .th().content("告警归属")
                .th().content("告警日期")
                .th().content("告警类型")
                .th().content("告警子类型")
                .th().content("告警级别")
                .th().content("负责人")
                .th().content("告警说明")._tr();
    }
}
