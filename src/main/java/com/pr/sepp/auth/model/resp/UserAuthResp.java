package com.pr.sepp.auth.model.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResp {
    private boolean hasRouterAuth;
    private boolean hasLoginAuth;
}
