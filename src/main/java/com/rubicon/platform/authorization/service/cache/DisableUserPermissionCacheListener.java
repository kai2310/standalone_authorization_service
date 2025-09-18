package com.rubicon.platform.authorization.service.cache;

import org.springframework.transaction.annotation.Transactional;

public class DisableUserPermissionCacheListener implements CacheNotificationListener<Long>
{
    DisableUserPermissionObjectCache cache;

    public DisableUserPermissionCacheListener(DisableUserPermissionObjectCache cache)
    {
        this.cache = cache;
    }

    @Override
    @Transactional(readOnly = true)
    public void onCreate(Long id)
    {
        cache.addEntry(id);
    }

    @Override
    @Transactional(readOnly = true)
    public void onUpdate(Long id)
    {
        cache.updateEntry(id);
    }

    @Override
    @Transactional(readOnly = true)
    public void onDelete(Long id)
    {
        cache.removeEntry(id);
    }
}
