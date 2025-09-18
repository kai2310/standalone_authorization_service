package com.rubicon.platform.authorization.service.cache;


import org.springframework.stereotype.Component;

/**
 */
@Component
public class NoOpCacheNotificationListener implements CacheNotificationListener<Long>
{
    @Override
    public void onCreate(Long aLong)
    {

    }

    @Override
    public void onUpdate(Long aLong)
    {

    }

    @Override
    public void onDelete(Long aLong)
    {

    }
}
