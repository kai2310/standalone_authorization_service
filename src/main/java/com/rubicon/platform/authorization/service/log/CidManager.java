package com.rubicon.platform.authorization.service.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class CidManager
{
    private Map<Thread,String> threadStatusMap = new ConcurrentHashMap<Thread, String>();

    private static final CidManager instance = new CidManager();

    public static CidManager getInstance()
    {
        return instance;
    }

    private CidManager()
    {
    }

    public void associateCorrelationId(String correlationId)
    {
        associateCorrelationId(correlationId,Thread.currentThread());
    }

    public void associateCorrelationId(String correlationId,Thread thread)
    {
        threadStatusMap.put(thread,correlationId);
    }

    public String getAssociatedCorrelationId()
    {
        return getAssociatedCorrelationId(Thread.currentThread());
    }

    public String getAssociatedCorrelationId(Thread thread)
    {
        return threadStatusMap.get(thread);
    }

    public String clearAssociatedCorrelationId()
    {
        return clearAssociatedCorrelationId(Thread.currentThread());
    }

    public String clearAssociatedCorrelationId(Thread thread)
    {
        return threadStatusMap.remove(thread);
    }

}
