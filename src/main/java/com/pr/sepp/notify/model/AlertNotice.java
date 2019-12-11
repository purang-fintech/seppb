package com.pr.sepp.notify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rendersnake.HtmlAttributes;
import org.rendersnake.HtmlCanvas;

import java.io.IOException;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertNotice {

    private Long warningId;
    private Integer userId;
    private String userName;
    private String userEmail;
    private Integer type;
    private Integer subType;
    private String category;
    private LocalDate warningDate;
    private Integer level;
    private String summary;
    private String content;


    public static HtmlCanvas buildTableHeader(HtmlCanvas htmlCanvas) throws IOException {
        HtmlAttributes attributes = new HtmlAttributes();
        attributes.add("onmouseover","this.style.backgroundColor='#ffff66';");
        attributes.add("onmouseout","this.style.backgroundColor='#d4e3e5';");
        return htmlCanvas.tr(attributes).th().content("告警类型")
                .th().content("告警归属")
                .th().content("告警日期")
                .th().content("告警级别")
                .th().content("告警详情")._tr();
    }

}
