package com.pr.sepp.utils.fasfdfs.conn;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ConnectionPoolConfig extends GenericObjectPoolConfig {
    public static final int FDFS_MAX_TOTAL = 500;

    public static final boolean FDFS_TEST_WHILE_IDLE = true;

    public static final boolean FDFS_BLOCK_WHEN_EXHAUSTED = true;

    public static final long FDFS_MAX_WAIT_MILLIS = 10000;

    public static final long FDFS_MIN_EVICTABLE_IDLETIME_MILLIS = 180000;

    public static final long FDFS_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 60000;

    public static final int FDFS_NUM_TESTS_PEREVICTION_RUN = -1;

    public ConnectionPoolConfig() {
        // 从池中借出的对象的最大数目
        setMaxTotal(FDFS_MAX_TOTAL);
        // 在空闲时检查有效性
        setTestWhileIdle(FDFS_TEST_WHILE_IDLE);
        // 连接耗尽时是否阻塞(默认true)
        setBlockWhenExhausted(FDFS_BLOCK_WHEN_EXHAUSTED);
        // 获取连接时的最大等待毫秒数100
        setMaxWaitMillis(FDFS_MAX_WAIT_MILLIS);
        // 视休眠时间超过了180秒的对象为过期
        setMinEvictableIdleTimeMillis(FDFS_MIN_EVICTABLE_IDLETIME_MILLIS);
        // 每过60秒进行一次后台对象清理的行动
        setTimeBetweenEvictionRunsMillis(FDFS_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
        // 清理时候检查所有线程
        setNumTestsPerEvictionRun(FDFS_NUM_TESTS_PEREVICTION_RUN);

        setTestOnBorrow(true);

        setTestOnCreate(false);

        setTestOnReturn(false);

        setJmxEnabled(false);
    }
}
