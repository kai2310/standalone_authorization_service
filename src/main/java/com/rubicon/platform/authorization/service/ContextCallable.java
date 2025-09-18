package com.rubicon.platform.authorization.service;

import com.codahale.metrics.Timer;
import com.dottydingo.service.tracelog.Trace;
import com.dottydingo.service.tracelog.TraceManager;
import com.rubicon.platform.authorization.service.log.CidManager;
import com.rubicon.platform.authorization.service.log.RequestLogFilter;
import org.slf4j.MDC;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
*/
public class ContextCallable<T> implements Callable
{
    private String cid;
    private Callable<T> wrapped;
    private DeferredResult<T> deferredResult;
    private Timer timer;
    private TraceManager traceManager;
    private Trace trace;

    public ContextCallable(String cid, Callable<T> wrapped, DeferredResult<T> deferredResult, Timer timer,
                           TraceManager traceManager, Trace trace)
    {
        this.cid = cid;
        this.wrapped = wrapped;
        this.deferredResult = deferredResult;
        this.timer = timer;
        this.traceManager = traceManager;
        this.trace = trace;
    }

    @Override
    public DeferredResult<T> call() throws Exception
    {
        CidManager.getInstance().associateCorrelationId(cid);
        MDC.put(RequestLogFilter.CID, cid);

        if(trace != null)
            traceManager.associateTrace(trace);

        long start = System.currentTimeMillis();
        try
        {
            T result = wrapped.call();
            deferredResult.setResult(result);
        }
        catch (Throwable t)
        {
            deferredResult.setErrorResult(t);
        }
        finally
        {
            timer.update(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            if(trace != null)
                traceManager.associateTrace(trace);
            MDC.clear();
            CidManager.getInstance().clearAssociatedCorrelationId();
        }

        return null;
    }
}
