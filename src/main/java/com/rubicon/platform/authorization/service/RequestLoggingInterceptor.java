package com.rubicon.platform.authorization.service;


import com.dottydingo.service.tracelog.Trace;
import com.dottydingo.service.tracelog.TraceManager;
import com.rubicon.platform.authorization.commons.requestlogger.*;
import com.rubicon.platform.authorization.springmvc.CORSInterceptor;
import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import com.rubicon.platform.authorization.service.log.CidManager;
import com.rubicon.platform.authorization.service.log.RequestLogFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 */
public class RequestLoggingInterceptor extends CORSInterceptor
{
    protected static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";

    private Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private Logger requestLogger = LoggerFactory.getLogger("com.rubiconproject.logs.requests");

    protected RequestBodyUtil requestBodyUtil = new RequestBodyUtil();
    protected ExtendedLogFormatter formatter = new ExtendedLogFormatter();
    private  boolean logOptionsCalls = false;

    @Autowired
    private TraceManager traceManager;

    public void setLogOptionsCalls(boolean logOptionsCalls)
    {
        this.logOptionsCalls = logOptionsCalls;
    }

    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult)
            throws Exception
    {
        super.beforeConcurrentHandling(request, deferredResult);
    }

    @Override
    public <T> void afterCompletion(NativeWebRequest req, DeferredResult<T> deferredResult) throws Exception
    {

        LogRequestWrapper request = req.getNativeRequest(LogRequestWrapper.class);
        LogResponseWrapper responseWrapper = req.getNativeResponse(LogResponseWrapper.class);

        if(request == null || responseWrapper == null)
        {
            logger.error("Missing wrapped entities, skipping logging.");
            return;
        }

        Date start = (Date) request.getAttribute(RequestLogFilter.START_TIME);
        if(start == null)
            start = new Date();

        String rid = (String) request.getAttribute(RequestLogFilter.CID);

        ExtendedLogEntry entry = new ExtendedLogEntry();
        entry.setIpAddress(getRequestIp(request));
        entry.setUserId(getUser(request));
        entry.setRequestTimestamp(start);
        entry.setRequestMethod(request.getMethod());
        entry.setRequestUri(request.getRequestURI());
        entry.setQueryString(request.getQueryString());
        entry.setStatusCode(responseWrapper.getStatus() == 0 ? 200 : responseWrapper.getStatus());
        entry.setResponseSize(responseWrapper.getResponseSize());
        entry.setResponseTimeMs(System.currentTimeMillis() - start.getTime());
        entry.setReferrer(request.getHeader("referer"));
        entry.setUserAgent(request.getHeader("user-agent"));
        entry.setRequestId(rid);
        if(shouldLogBody(request))
            entry.setRequestBody(filterBody(request.getRequestBody()));

        Trace trace = (Trace) request.getAttribute(RequestLogFilter.TRACE);
        if(trace != null )
        {
            traceManager.associateTrace(trace);
            logger.debug("Response Time: {}",entry.getResponseTimeMs());
            logger.debug("Response Status: {}",entry.getStatusCode());
            traceManager.disassociateTrace();
            try
            {
                trace.close();
            }
            catch (Exception e)
            {
                logger.warn("Error closing trace.",e);
            }
        }

        MDC.clear();
        CidManager.getInstance().clearAssociatedCorrelationId();

        boolean isOptionsCall = request.getMethod().equalsIgnoreCase("OPTIONS");
        if (!isOptionsCall || (isOptionsCall && logOptionsCalls))
        {
            requestLogger.info(formatter.formatEntry(entry));
        }
    }

    /**
     * Determine if the body of the request should be logged. The default implementation will log the body if the
     * request method is POST or PUT. Subclasses should override this to change the behavior.
     * @param request The request
     * @return True if the request body should be logged
     */
    protected boolean shouldLogBody(HttpServletRequest request)
    {
        return request.getMethod().equals("POST") || request.getMethod().equals("PUT");
    }

    /**
     * Filter the request body for logging. THe default implementation escapes quotes and removes excess white space.
     * Subclasses should override this method to change the behavior.
     * @param body The request body.
     * @return The filtered request body.
     */
    protected String filterBody(String body)
    {
        if(StringUtils.isEmpty(body))
            return body;

        String filtered = requestBodyUtil.escapeQuotes(body);
        filtered = requestBodyUtil.filterWhitespace(filtered);
        return filtered;
    }

    /**
     * Return the requesting host IP address. The default implementation checks for the X-Forwarded-For header and, if
     * it exists, will use the left most IP address from this header as the requesting host IP address. If it does not
     * exist it will use the provided remote address from the request. Subclasses should override this method to
     * change the behavior.
     * @param request The request
     * @return The request IP address
     */
    protected String getRequestIp(HttpServletRequest request)
    {
        String header = request.getHeader(FORWARDED_FOR_HEADER);
        if(StringUtils.isEmpty(header))
            return request.getRemoteAddr();

        String[] split = header.split(",");
        return split[0];
    }

    protected String getUser(HttpServletRequest request)
    {
        Object user = request.getAttribute(AuthorizationInterceptor.USER_INFO);
        if(user == null)
            return null;
        if(!(user instanceof UserSelf))
            return null;

        return ((UserSelf)user).getUsername();
    }

}
