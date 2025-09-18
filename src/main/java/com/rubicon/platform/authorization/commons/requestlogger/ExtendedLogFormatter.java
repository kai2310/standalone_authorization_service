package com.rubicon.platform.authorization.commons.requestlogger;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * User: mhellkamp
 * Date: 9/27/12
 */
public class ExtendedLogFormatter
{
    private static final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.S Z");
    private static final String formatString =
            "%s %s %s [%s] \"%s %s\" %d %d %d \"%s\" \"%s\" \"%s\" \"%s\"";

    private static final String DEFAULT = "-";

    public String formatEntry(ExtendedLogEntry entry)
    {
        String requestUri = entry.getRequestUri();
        if(!isEmpty(entry.getQueryString()))
        {
            requestUri += "?" + entry.getQueryString();
        }
        return String.format(formatString,
                getValueOrDefault(entry.getIpAddress()),
                getValueOrDefault(replaceSpaces(entry.getUserIdentifier())),
                getValueOrDefault(replaceSpaces(entry.getUserId())),
                dateFormat.format(entry.getRequestTimestamp()),
                getValueOrDefault(entry.getRequestMethod()),
                requestUri,
                entry.getStatusCode(),
                entry.getResponseSize(),
                entry.getResponseTimeMs(),
                getValueOrDefault(entry.getReferrer(),""),
                getValueOrDefault(entry.getUserAgent(),""),
                getValueOrDefault(entry.getRequestId(),""),
                getValueOrDefault(entry.getRequestBody(),"")
        );
    }

    private String getValueOrDefault(String value)
    {
        return getValueOrDefault(value,DEFAULT);
    }

    private String getValueOrDefault(String value, String defaultValue)
    {
        return isEmpty(value) ? defaultValue : value;
    }

    private boolean isEmpty(String value) {return value == null || value.length() == 0;}

    private String replaceSpaces(String value)
    {
        if(value == null)
            return null;

        return value.replaceAll(" ","_");
    }
}
