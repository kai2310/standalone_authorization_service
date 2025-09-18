package com.rubicon.platform.authorization.service.jobs;

import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.Date;

/**
 */
@ManagedResource
public class CacheReloadJobStatus extends JobStatus
{
    private long lastRefreshedTime = 0;

    public void refreshed()
    {
        lastRefreshedTime = System.currentTimeMillis();
    }

    public String getLastRefreshedTime()
    {
        if (lastRefreshedTime == 0)
            return "";

        return new Date(lastRefreshedTime).toString();
    }
}
