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
    private static Map<Integer, Long> warningsCountMap;

    static {
        Cache<String, Set<WebSocketSession>> userCache = CacheBuilder.newBuilder().maximumSize(200).build();
        userSessionMap = userCache.asMap();
        warningsCountMap = Maps.newConcurrentMap();
        jobSessionMap = Maps.newConcurrentMap();
    }

    public static Map<String, Set<WebSocketSession>> getUserSessionMap() {
        return userSessionMap;
    }

    public static Map<DeploymentWebSessionPayload, Set<WebSocketSession>> getJobSessionMap() {
        return jobSessionMap;
    }

    public static Map<Integer, Long> buildWarningsCountMap(Integer userId, Long val) {
        warningsCountMap.put(userId, val);
        return warningsCountMap;
    }

    public static synchronized Long getWarningCountLast(Integer userId) {
        Long lastValue = warningsCountMap.get(userId);
        if (Objects.isNull(lastValue)) {
            return 0L;
        }
        return lastValue;
    }

    public static void clear() {
        userSessionMap.clear();
        warningsCountMap.clear();
        jobSessionMap.clear();
    }
}
