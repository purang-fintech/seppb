package com.pr.sepp.common.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentWebSessionPayload {

    private String jobName;
    private Integer envType;
    private Integer branchId;

    public static DeploymentWebSessionPayload apply(TextMessage textMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(textMessage.getPayload(), DeploymentWebSessionPayload.class);
    }

}
