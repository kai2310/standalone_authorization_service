package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.service.client.IdmClient;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

public class UserEmailCacheEntryFactory implements CacheEntryFactory
{
    private IdmClient idmClient;

    @Override
    public Object createEntry(Object email) throws Exception
    {
        return idmClient.getUserByEmail(email.toString());
    }

    public void setIdmClient(IdmClient idmClient)
    {
        this.idmClient = idmClient;
    }
}
