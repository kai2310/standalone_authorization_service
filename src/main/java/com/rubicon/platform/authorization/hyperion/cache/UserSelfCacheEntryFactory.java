package com.rubicon.platform.authorization.hyperion.cache;

import com.rubicon.platform.authorization.service.client.revv4.model.UserSelf;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class UserSelfCacheEntryFactory implements CacheEntryFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSelfCacheEntryFactory.class);

    private RestTemplate restTemplate;
    private String requestUrl;

    public void setRestTemplate(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    public void setRequestUrl(String requestUrl)
    {
        this.requestUrl = requestUrl;
    }

    @Override
    public Object createEntry(Object key)
    {
        try
        {
            return restTemplate.getForObject(requestUrl, UserSelf.class, key);
        }
        catch (HttpClientErrorException e)
        {
            int value = e.getStatusCode().value();
            if (value != 401 && value != 404)
            {
                LOGGER.error(String.format("Error retrieving UserSelf from %s", requestUrl), e);
            }
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("Error retrieving UserSelf from %s", requestUrl), e);
        }

        return null;
    }
}
