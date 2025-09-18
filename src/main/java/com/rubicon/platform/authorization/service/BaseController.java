package com.rubicon.platform.authorization.service;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.dottydingo.service.tracelog.Trace;
import com.dottydingo.service.tracelog.TraceManager;
import com.rubicon.platform.authorization.service.log.CidManager;
import com.rubicon.platform.authorization.service.log.RequestLogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 */
public abstract class BaseController
{
    private ConcurrentMap<String,Timer> timerMap = new ConcurrentHashMap<String, Timer>();

    @Autowired
    private MetricRegistry metricRegistry;

    protected ExecutorService executorService;

    protected long timeout;

    @Autowired
    protected TraceManager traceManager;

    public void setExecutorService(ExecutorService executorService)
    {
        this.executorService = executorService;
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }


    protected Timer getTimer(String action)
    {
        String key = String.format("service_%s_%s", getEndpointName(),action.toLowerCase());
        Timer timer = timerMap.get(key);
        if(timer == null)
        {
            timer = metricRegistry.timer(MetricRegistry.name("request", key));
            Timer previous = timerMap.putIfAbsent(key,timer);
            if(previous != null)
                timer = previous;
        }
        return timer;
    }

    abstract protected String getEndpointName();

    protected <T> DeferredResult<T> submit(Callable<T> callable, Timer timer, HttpServletRequest httpServletRequest)
    {
        Trace trace = (Trace) httpServletRequest.getAttribute(RequestLogFilter.TRACE);
        DeferredResult<T> result = new DeferredResult<T>(timeout);

        String cid = CidManager.getInstance().getAssociatedCorrelationId();

        executorService.submit(new ContextCallable<T>(cid, callable,result,timer,traceManager,trace));
        return result;
    }
}
