package com.pr.sepp.notify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alert {
    //w.id,w.warningDate warningDate,w.category, w.type,l.levelName levelName,w.summary
    private Integer id;
    private Integer type;
    private String category;
    private LocalDate warningDate;
    private Integer level;
    private String summary;
    private String content;

}
