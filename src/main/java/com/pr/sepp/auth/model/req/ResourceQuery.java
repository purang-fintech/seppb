package com.pr.sepp.auth.model.req;

import lombok.Builder;
import lombok.Data;

import static java.util.Objects.isNull;

@Data
@Builder
public class ResourceQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    private Integer productId;

    public Integer getPageNum() {
        if (isNull(pageNum)) {
            return 1;
        }
        return pageNum;
    }

    public Integer getPageSize() {
        if (isNull(pageSize)) {
            return 10;
        }
        return pageSize;
    }
}
