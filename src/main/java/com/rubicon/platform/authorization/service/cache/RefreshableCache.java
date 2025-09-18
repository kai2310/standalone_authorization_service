package com.rubicon.platform.authorization.service.cache;

import com.dottydingo.hyperion.api.ApiObject;

/**
 */
public interface RefreshableCache
{
    void refresh();

    String getEndpointName();
}
