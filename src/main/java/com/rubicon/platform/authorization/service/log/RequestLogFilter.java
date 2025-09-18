package com.rubicon.platform.authorization.service.log;

import com.dottydingo.service.tracelog.Trace;
import com.dottydingo.service.tracelog.TraceFactory;
import com.dottydingo.service.tracelog.TraceManager;
import com.dottydingo.service.tracelog.TraceType;
import com.rubicon.platform.authorization.commons.requestlogger.LogRequestWrapper;
import com.rubicon.platform.authorization.commons.requestlogger.LogResponseWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

/**
 */
@Component("requestLoggingFilter")
public class RequestLogFilter implements Filter
{
    public static final String REQUEST_ID_HEADER = "RubiconProject-RequestId";
    public static final String RP_CID = "RP-CID";
    public static final String START_TIME = "START_TIME";
    public static final String CID = "CID";
    public static final String TRACE = "TRACE";

    private static Logger logger = LoggerFactory.getLogger(RequestLogFilter.class);


    private String host = "unknown";

    @Autowired
    private TraceFactory traceFactory;

    @Autowired
    private TraceManager traceManager;

    @PostConstruct
    public void init()
    {
        try
        {
            host = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ignore){}

    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        init();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        LogRequestWrapper requestWrapper = new LogRequestWrapper(request);
        LogResponseWrapper responseWrapper = new LogResponseWrapper(response);

        String rid = getRequestId(request);
        CidManager.getInstance().associateCorrelationId(rid);

        Trace trace = null;
        String traceParam = request.getParameter("trace");
        if(StringUtils.isNotEmpty(traceParam))
        {
            String[] split = traceParam.split(":");
            try
            {
                TraceType type = TraceType.valueOf(split[0]);
                if(type == TraceType.email && split.length == 2)
                    trace = traceFactory.createTrace(type, split[1]);
                else if(type == TraceType.file)
                    trace = traceFactory.createTrace(type, rid + ".trace");
            }
            catch (Exception e)
            {
                logger.warn("Could not create trace.",e);
            }

        }

        if(trace != null)
        {
            traceManager.associateTrace(trace);
            logger.debug("Host: {}",host);
            logger.debug("Client IP: {}",getRequestIp(request));
            logger.debug("Request Method: {}",request.getMethod());
            logger.debug("Request URI: {}",request.getRequestURI());
            logger.debug("Query String: {}",request.getQueryString());
            logger.debug("Correlation ID: {}",rid);
            traceManager.disassociateTrace();
        }


        MDC.put(CID, rid);

        request.setAttribute(START_TIME,new Date());
        request.setAttribute(CID,rid);
        request.setAttribute(TRACE,trace);

        try
        {
            chain.doFilter(requestWrapper,responseWrapper);
        }
        finally
        {
            MDC.clear();
            CidManager.getInstance().clearAssociatedCorrelationId();
        }


    }


    @Override
    public void destroy()
    {

    }

    private String getRequestIp(HttpServletRequest request)
    {
        String header = request.getHeader("X-Forwarded-For");
        if(StringUtils.isEmpty(header))
            return request.getRemoteAddr();

        String[] split = header.split(",");
        return split[0];

    }

    /**
     * Return the request ID for the request. The default implementation returns the value of tbe RubiconProject-RequestId
     * header if it exists, otherwise it generates a random UUID. Subclasses should override this method to change
     * the behavior.
     * @param request The request
     * @return The RID.
     */
    protected String getRequestId(HttpServletRequest request)
    {
        String requestId = request.getHeader(RP_CID);
        if (StringUtils.isEmpty(requestId))
        {
            requestId = request.getHeader(REQUEST_ID_HEADER);
            if (StringUtils.isEmpty(requestId))
            {
                requestId = UUID.randomUUID().toString();
            }
        }
        return requestId;
    }
}
