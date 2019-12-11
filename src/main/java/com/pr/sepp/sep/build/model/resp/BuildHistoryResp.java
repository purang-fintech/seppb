package com.pr.sepp.sep.build.model.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pr.sepp.sep.build.model.BuildHistory;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildHistoryResp {

    private String tag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createdDate;
    private List<BuildHistory> buildHistories;


    public static List<BuildHistoryResp> mapToBuildHistoryResps(Map<String, List<BuildHistory>> buildHistoriesMap) {
        List<BuildHistoryResp> buildHistoryResps = Lists.newArrayList();
        if (buildHistoriesMap == null) {
            return buildHistoryResps;
        }
        buildHistoriesMap.forEach((key, value) -> {
            BuildHistoryResp buildHistoryResp = BuildHistoryResp.builder()
                    .tag(key)
                    .createdDate(value.get(0).getCreatedDate())
                    .buildHistories(value)
                    .build();
            buildHistoryResps.add(buildHistoryResp);
        });
        return buildHistoryResps.stream()
                .sorted(Comparator.comparing(BuildHistoryResp::getCreatedDate).reversed())
                .collect(Collectors.toList());
    }

}
