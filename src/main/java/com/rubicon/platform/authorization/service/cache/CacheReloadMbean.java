package com.rubicon.platform.authorization.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 */
@ManagedResource
@Component
public class CacheReloadMbean
{
    @Autowired
    private CacheReloadController cacheReloadController;

    @ManagedOperation(description = "Reload the caches")
    public void reloadCache()
    {
        cacheReloadController.reloadCaches();
    }

    public String getLastStartTime()
    {
        return asString(cacheReloadController.getLastStartTime());
    }

    public String getLastEndTime()
    {
        return asString(cacheReloadController.getLastEndTime());
    }

    public boolean isRunning()
    {
        return cacheReloadController.isRunning();
    }

    public String getLastError()
    {
        Throwable lastError = cacheReloadController.getLastError();
        if(lastError == null)
            return  "";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        lastError.printStackTrace(ps);
        ps.flush();

        return os.toString();
    }

    public String getLastErrorTime()
    {
        return asString(cacheReloadController.getLastErrorTime());
    }

    private String asString(long time)
    {
        if(time == 0)
            return "";

        return new Date(time).toString();
    }
}
