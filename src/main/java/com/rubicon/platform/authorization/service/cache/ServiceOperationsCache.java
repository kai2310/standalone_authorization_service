package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.model.data.acm.BaseRoleApiObject;

import java.util.List;

/**
 */
public interface ServiceOperationsCache<T extends BaseRoleApiObject> extends DataCache<T,Long>
{
    ServiceOperationsHolder<T> getServiceOperationsHolder(Long id);

    List<ServiceOperationsHolder<T>> loadAll();
}
