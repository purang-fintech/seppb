package com.pr.sepp.utils.jenkins.pool;

import com.pr.sepp.common.exception.SeppServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.Closeable;
import java.util.NoSuchElementException;

@Slf4j
public abstract class Pool<T> implements Closeable {

    protected GenericObjectPool<T> internalPool;

    public Pool(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {
        initPool(poolConfig, factory);
    }

    public void initPool(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {
        if (this.internalPool != null) {
            try {
                closeInternalPool();
            } catch (Exception e) {
                log.error("", e);
            }
        }
        this.internalPool = new GenericObjectPool<>(factory, poolConfig);
    }

    protected void closeInternalPool() {
        try {
            internalPool.close();
        } catch (Exception e) {
            throw new SeppServerException("Could not destroy the pool", e);
        }
    }

    @Override
    public void close() {
        destroy();
    }

    public void destroy() {
        closeInternalPool();
    }


    public T getResource() {
        try {
            return internalPool.borrowObject();
        } catch (NoSuchElementException nse) {
            if (null == nse.getCause()) {
                throw new SeppServerException(
                        "Could not get a resource since the pool is exhausted", nse);
            }
            throw new SeppServerException("Could not get a resource from the pool", nse);
        } catch (Exception e) {
            throw new SeppServerException("Could not get a resource from the pool", e);
        }
    }

    protected void returnResourceObject(final T resource) {
        if (resource == null) {
            return;
        }
        try {
            internalPool.returnObject(resource);
        } catch (Exception e) {
            throw new SeppServerException("Could not return the resource to the pool", e);
        }
    }

    protected void returnBrokenResource(final T resource) {
        if (resource != null) {
            returnBrokenResourceObject(resource);
        }
    }

    protected void returnResource(final T resource) {
        if (resource != null) {
            returnResourceObject(resource);
        }
    }

    protected void returnBrokenResourceObject(final T resource) {
        try {
            internalPool.invalidateObject(resource);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    protected void printJenkinsPoolLog() {
        if (log.isDebugEnabled()) {
            log.info("jenkinsPool{maxTotal={},maxIdle={},numActive={},numIdle={},destroyCount={},createdCount={}}",
                    internalPool.getMaxTotal(), internalPool.getMaxIdle(),
                    internalPool.getNumActive(), internalPool.getNumIdle(), internalPool.getDestroyedCount(),
                    internalPool.getCreatedCount());
        }
    }
}
