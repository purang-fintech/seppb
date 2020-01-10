package com.pr.sepp.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.pr.sepp.common.websocket.model.DeploymentWebSessionPayload;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public final class GlobalCache {
    private GlobalCache() {

    }
    public static final ConcurrentMap<String, Set<WebSocketSession>> userSessionMap;
    public static final Map<DeploymentWebSessionPayload, Set<WebSocketSession>> jobSessionMap;
    //记录每次session的告警值
    private static Map<Integer, Long> alertsCountMap;

    static {
        Cache<String, Set<WebSocketSession>> userCache = CacheBuilder.newBuilder().maximumSize(200).build();
        userSessionMap = userCache.asMap();
        alertsCountMap = Maps.newConcurrentMap();
        jobSessionMap = Maps.newConcurrentMap();
    }

    public static Map<String, Set<WebSocketSession>> getUserSessionMap() {
        return userSessionMap;
    }

    public static Map<DeploymentWebSessionPayload, Set<WebSocketSession>> getJobSessionMap() {
        return jobSessionMap;
    }

    public static Map<Integer, Long> buildAlertsCountMap(Integer userId, Long val) {
        alertsCountMap.put(userId, val);
        return alertsCountMap;
    }

    public static synchronized Long getAlertCountLast(Integer userId) {
        Long lastValue = alertsCountMap.get(userId);
        if (Objects.isNull(lastValue)) {
            return 0L;
        }
        return lastValue;
    }

    public static void clear() {
        userSessionMap.clear();
        alertsCountMap.clear();
        jobSessionMap.clear();
    }
}
