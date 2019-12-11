package com.pr.sepp.common.websocket.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pr.sepp.common.GlobalCache;
import com.pr.sepp.common.websocket.GlobalSession;
import com.pr.sepp.common.websocket.model.DeploymentWebSessionPayload;
import com.pr.sepp.sep.build.model.BuildHistory;
import com.pr.sepp.sep.build.model.DeploymentBuild;
import com.pr.sepp.sep.build.model.DeploymentHistory;
import com.pr.sepp.sep.build.service.DeploymentService;
import com.pr.sepp.sep.build.service.impl.BuildHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.pr.sepp.sep.build.model.DeploymentBuild.applyDeploymentPipeline;

@Slf4j
@Component
public class DeploymentBuildServer implements WebSocketServer<DeploymentWebSessionPayload, DeploymentBuild> {

    @Autowired
    private BuildHistoryService buildHistoryService;
    @Autowired
    private DeploymentService deploymentService;

    @Autowired
    private ObjectMapper mapper;

    @Scheduled(fixedRate = 10 * 1000)
    @Override
    public void pushAll() {
        Set<DeploymentWebSessionPayload> jobNames = GlobalCache.getJobSessionMap().keySet();
        jobNames.forEach(this::pushByT);
    }

    @Override
    public void pushByT(DeploymentWebSessionPayload payload) {
        Set<WebSocketSession> sessions = GlobalCache.getJobSessionMap().get(payload);
        sessions.forEach(session -> pushBySession(session, payload));
    }

    @Override
    public void pushBySession(WebSocketSession session, DeploymentBuild deploymentBuild) {
        try {
            GlobalSession.sendMessage(session, mapper.writeValueAsString(deploymentBuild));
        } catch (IOException e) {
            log.error("推送构建部署信息失败:{}", e);
        }
    }

    public void pushBySession(WebSocketSession session, DeploymentWebSessionPayload payload) {
        List<DeploymentHistory> deploymentHistories = deploymentService
                .deploymentHistories(payload.getJobName(), payload.getEnvType(), payload.getBranchId());
        List<BuildHistory> buildHistories = buildHistoryService.buildHistories(payload.getJobName());
        pushBySession(session, DeploymentBuild.builder()
                .deploymentHistories(deploymentHistories)
                .buildHistories(buildHistories)
                .deploymentPipelines(applyDeploymentPipeline(deploymentHistories))
                .build());
    }
}
