package com.rubicon.platform.authorization.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 */
public class CacheReloadController
{
    private Logger logger = LoggerFactory.getLogger(CacheReloadController.class);
    private transient long lastStartTime;
    private transient long lastEndTime;
    private transient boolean running;
    private transient Throwable lastError;
    private transient long lastErrorTime;

    @Autowired
    private CacheLockHolder cacheLockHolder;

    private List<RefreshableCache> cacheLoaders;

    public void setCacheLoaders(List<RefreshableCache> cacheLoaders)
    {
        this.cacheLoaders = cacheLoaders;
    }

    public boolean reloadCaches()
    {
        boolean success = false;
        logger.info("Starting reload.");
        lastStartTime = System.currentTimeMillis();
        running = true;
        Lock lock = cacheLockHolder.getWriteLock();
        lock.lock();
        logger.info("Lock acquired.");
        try
        {
            for (RefreshableCache loader : cacheLoaders)
            {
                logger.info("Reloading endpoint {}",loader.getEndpointName());
                loader.refresh();
            }

            success = true;
        }
        catch (Throwable e)
        {
            lastError = e;
            lastErrorTime = System.currentTimeMillis();
            logger.error("Error reloading caches",e);
        }
        finally
        {
            lock.unlock();
        }

        running = false;
        lastEndTime = System.currentTimeMillis();
        logger.info("Cache reload complete.");
        return success;
    }

    public long getLastStartTime()
    {
        return lastStartTime;
    }

    public long getLastEndTime()
    {
        return lastEndTime;
    }

    public boolean isRunning()
    {
        return running;
    }

    public Throwable getLastError()
    {
        return lastError;
    }

    public long getLastErrorTime()
    {
        return lastErrorTime;
    }
}
