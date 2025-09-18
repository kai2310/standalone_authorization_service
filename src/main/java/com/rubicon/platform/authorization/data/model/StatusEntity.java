package com.rubicon.platform.authorization.data.model;

import com.dottydingo.hyperion.core.model.PersistentObject;
import com.rubicon.platform.authorization.model.data.acm.Status;

/**
 */
public interface StatusEntity extends PersistentObject<Long>
{
    Status getStatus();
    void setStatus(Status status);
}
