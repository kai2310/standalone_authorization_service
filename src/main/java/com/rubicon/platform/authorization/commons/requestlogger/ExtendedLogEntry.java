package com.rubicon.platform.authorization.commons.requestlogger;

import java.util.Date;

/**
 * An extended request log entry
 */
public class ExtendedLogEntry
{
    private String ipAddress;
    private String userIdentifier;
    private String userId;
    private Date requestTimestamp;
    private String requestMethod;
    private String requestUri;
    private String queryString;
    private int statusCode;
    private long responseSize;
    private long responseTimeMs;
    private String referrer;
    private String userAgent;
    private String requestId;
    private String requestBody;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getUserIdentifier()
    {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier)
    {
        this.userIdentifier = userIdentifier;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public Date getRequestTimestamp()
    {
        return requestTimestamp;
    }

    public void setRequestTimestamp(Date requestTimestamp)
    {
        this.requestTimestamp = requestTimestamp;
    }

    public String getRequestMethod()
    {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod)
    {
        this.requestMethod = requestMethod;
    }

    public String getRequestUri()
    {
        return requestUri;
    }

    public void setRequestUri(String requestUri)
    {
        this.requestUri = requestUri;
    }

    public String getQueryString()
    {
        return queryString;
    }

    public void setQueryString(String queryString)
    {
        this.queryString = queryString;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public long getResponseSize()
    {
        return responseSize;
    }

    public void setResponseSize(long responseSize)
    {
        this.responseSize = responseSize;
    }

    public long getResponseTimeMs()
    {
        return responseTimeMs;
    }

    public void setResponseTimeMs(long responseTimeMs)
    {
        this.responseTimeMs = responseTimeMs;
    }

    public String getReferrer()
    {
        return referrer;
    }

    public void setReferrer(String referrer)
    {
        this.referrer = referrer;
    }

    public String getUserAgent()
    {
        return userAgent;
    }

    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public String getRequestBody()
    {
        return requestBody;
    }

    public void setRequestBody(String requestBody)
    {
        this.requestBody = requestBody;
    }
}
