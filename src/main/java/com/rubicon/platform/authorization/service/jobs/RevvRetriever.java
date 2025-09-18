package com.rubicon.platform.authorization.service.jobs;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.rubicon.platform.authorization.service.utils.MetricUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * User: mhellkamp
 * Date: 10/23/12
 */
public class RevvRetriever<T extends ArrayList>
{
    protected static Logger logger = LoggerFactory.getLogger(RevvRetriever.class);

    protected static FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    protected RestTemplate restTemplate;
    protected String baseUrl;
    protected Class<T> responseType;
    protected String properties;
    protected String accountType;
    protected MetricUtils metricUtils;

    private ConcurrentMap<String, Meter> meterMap = new ConcurrentHashMap<String, Meter>();

    public void setRestTemplate(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public void setResponseType(Class<T> responseType)
	{
		this.responseType = responseType;
	}

    public void setProperties(String properties)
    {
        this.properties = properties;
    }

    public void setMetricUtils(MetricUtils metricUtils)
    {
        this.metricUtils = metricUtils;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public T retrieve(long since, String status)
    {
        List<String> params = new ArrayList<String>();
		if (since > 0)
			params.add(String.format("updatedSince=%s",format.format(new Date(since))));

		if(status != null)
			params.add(String.format("status=%s", status));

        if(properties != null)
            params.add(String.format("properties=%s",properties));

        String query = "";
        if(params.size() > 0)
            query = StringUtils.join(params,"&");

        String requestUrl = baseUrl;
        if(query.length() > 0)
            requestUrl+="?"+query;

        T accountData = null;
        Counter counter = metricUtils.getCounter(accountType, "accountsync");
        try
        {
            accountData = restTemplate.getForObject(requestUrl, responseType);
            // Reset the counter to zero after a successful run
            counter.dec(counter.getCount());
        }
        catch (Exception e)
        {
            counter.inc();
            logger.warn(String.format("Error calling Revv API for %s.", baseUrl), e);

            accountData = null;
        }

        return accountData;
    }


}
