package com.pr.sepp.file.model;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
public class TestMail {
    private String body;
    private String subject;
    private String[] to;
    private String[] cc;
    private String imgUrl;

    public static String[] ccEmailApply(String[] toCC) {
        return Arrays.stream(toCC)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList()).toArray(new String[]{});
    }
}
