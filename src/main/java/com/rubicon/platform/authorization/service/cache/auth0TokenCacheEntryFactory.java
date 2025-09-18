package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.service.client.Auth0Client;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

public class auth0TokenCacheEntryFactory implements CacheEntryFactory
{
    private Auth0Client auth0Client;

    @Override
    public Object createEntry(Object token) throws Exception
    {
        return auth0Client.getUserInfo(token.toString());
    }

    public void setAuth0Client(Auth0Client auth0Client)
    {
        this.auth0Client = auth0Client;
    }
}
