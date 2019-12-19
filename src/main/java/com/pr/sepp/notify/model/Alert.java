package com.pr.sepp.notify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alert {
	private Integer id;
	private Integer productId;
	private Integer type;
	private Integer subType;
	private String category;
	private LocalDate warningDate;
	private Integer level;
	private Integer responser;
	private String summary;
	private String content;
}
