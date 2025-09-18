package com.rubicon.platform.authorization.hyperion.log;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.dottydingo.hyperion.core.endpoint.HttpMethod;
import com.dottydingo.hyperion.core.endpoint.HyperionContext;
import com.dottydingo.service.endpoint.RequestLogHandler;
import com.dottydingo.service.endpoint.context.EndpointRequest;
import com.dottydingo.service.endpoint.context.EndpointResponse;
import com.dottydingo.service.endpoint.context.UserContext;
import com.dottydingo.service.endpoint.io.BufferingInputStreamWrapper;
import com.dottydingo.service.endpoint.io.SizeTrackingOutputStream;
import com.rubicon.platform.authorization.commons.requestlogger.ExtendedLogEntry;
import com.rubicon.platform.authorization.commons.requestlogger.ExtendedLogFormatter;
import com.rubicon.platform.authorization.commons.requestlogger.RequestBodyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RubiconRequestLogHandler implements RequestLogHandler<HyperionContext>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RubiconRequestLogHandler.class);
    private RequestBodyUtil requestBodyUtil = new RequestBodyUtil();
    private ExtendedLogFormatter formatter = new ExtendedLogFormatter();
    private String requestLogName = "com.rubiconproject.logs.requests";
    private Logger requestLogger;
    private Map<String, Timer> timerMap = new HashMap<>();
    private Map<Integer, Meter> meterMap = new HashMap<>();
    private boolean logOptionsCalls = false;
    private MetricRegistry metricRegistry;

    public void init()
    {
        requestLogger = LoggerFactory.getLogger(requestLogName);
    }

    public void setRequestLogName(String requestLogName)
    {
        this.requestLogName = requestLogName;
    }

    public void setFormatter(ExtendedLogFormatter formatter)
    {
        this.formatter = formatter;
    }

    public void setRequestBodyUtil(RequestBodyUtil requestBodyUtil)
    {
        this.requestBodyUtil = requestBodyUtil;
    }

    public void setLogOptionsCalls(boolean logOptionsCalls)
    {
        this.logOptionsCalls = logOptionsCalls;
    }

    public void setMetricRegistry(MetricRegistry metricRegistry)
    {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public void logRequest(HyperionContext context)
    {
        try
        {
            EndpointRequest request = context.getEndpointRequest();
            EndpointResponse response = context.getEndpointResponse();

            ExtendedLogEntry entry = new ExtendedLogEntry();
            entry.setIpAddress(getRequestIp(request));
            entry.setUserId(getUser(context.getUserContext()));
            entry.setRequestTimestamp(new Date(context.getStartTimestamp()));
            entry.setRequestMethod(getMethod(context));
            entry.setRequestUri(request.getRequestUri());
            entry.setQueryString(request.getQueryString());
            entry.setStatusCode(response.getResponseCode());

            entry.setResponseTimeMs(context.getElapsedTime());
            entry.setReferrer(request.getFirstHeader("referer"));
            entry.setUserAgent(request.getFirstHeader("user-agent"));
            entry.setRequestId(context.getCorrelationId());

            OutputStream os = response.getOutputStream();
            if (os instanceof SizeTrackingOutputStream)
            {
                entry.setResponseSize(((SizeTrackingOutputStream) os).getBytesWritten());
            }

            if (shouldLogBody(request))
            {
                InputStream is = request.getInputStream();
                if (is instanceof BufferingInputStreamWrapper)
                {
                    BufferingInputStreamWrapper wrapper = (BufferingInputStreamWrapper) is;
                    entry.setRequestBody(filterBody(new String(wrapper.getBuffer())));
                }
            }

            if (context.getEffectiveMethod() != HttpMethod.OPTIONS ||
                (context.getEffectiveMethod() == HttpMethod.OPTIONS && logOptionsCalls))
            {
                requestLogger.info(formatter.formatEntry(entry));
            }

            Timer timer = getTimer(context);
            if (timer != null)
            {
                timer.update(context.getElapsedTime(), TimeUnit.MILLISECONDS);
            }
            if(response.getResponseCode() >=400)
            {
                Meter meter = getMeter(response.getResponseCode());
                if(meter!=null)
                    meter.mark();
            }

        }
        catch (Exception e)
        {
            LOGGER.error(String.format("Error processing request log for CID: %s", context.getCorrelationId()), e);
        }
    }

    private String getMethod(HyperionContext context)
    {
        if(context.getEffectiveMethod() != null)
            return context.getEffectiveMethod().name();

        return context.getEndpointRequest().getRequestMethod();
    }

    private synchronized  Meter getMeter(int responseCode)
    {
        Meter meter = meterMap.get(responseCode);
        if(meter == null)
        {
            meter = metricRegistry.meter(MetricRegistry.name("request", responseCode + "_response"));
            meterMap.put(responseCode,meter);
        }

        return meter;
    }

    private synchronized Timer getTimer(HyperionContext context)
    {
        if (context.getEntityPlugin() == null)
        {
            return null;
        }

        if (context.getEffectiveMethod() == null)
        {
            return null;
        }

        String endpointName = context.getEntityPlugin().getEndpointName();
        if (context.isHistory())
        {
            endpointName = endpointName + "_history";
        }

        String key = String.format("data_%s_%s", endpointName.toLowerCase(),
                context.getEffectiveMethod().toString().toLowerCase());
        Timer timer = timerMap.get(key);
        if (timer == null)
        {
            timer = metricRegistry.timer(MetricRegistry.name("request", key));
            timerMap.put(key, timer);
        }


        return timer;
    }

    private boolean shouldLogBody(EndpointRequest request)
    {
        return (request.getRequestMethod().equals("POST") || request.getRequestMethod().equals("PUT"));

    }

    private String getUser(UserContext userContext)
    {
        if (userContext == null)
        {
            return null;
        }

        return userContext.getUserName();

    }

    private String filterBody(String body)
    {
        if (StringUtils.isEmpty(body))
        {
            return body;
        }

        String filtered = requestBodyUtil.escapeQuotes(body);
        filtered = requestBodyUtil.filterWhitespace(filtered);
        return filtered;
    }

    private String getRequestIp(EndpointRequest request)
    {
        String header = request.getFirstHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(header))
        {
            return request.getHttpServletRequest().getRemoteAddr();
        }

        String[] split = header.split(",");
        return split[0];

    }
}
