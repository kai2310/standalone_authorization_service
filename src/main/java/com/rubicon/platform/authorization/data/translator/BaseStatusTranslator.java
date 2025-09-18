package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.api.AuditableApiObject;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.DefaultAuditingTranslator;
import com.dottydingo.hyperion.core.translation.ObjectWrapper;
import com.rubicon.platform.authorization.data.model.BaseStatusPersistentObject;
import com.rubicon.platform.authorization.model.data.acm.Status;

/**
 */
public abstract class BaseStatusTranslator<C extends AuditableApiObject,P extends BaseStatusPersistentObject>
        extends DefaultAuditingTranslator<C,P>
{
    protected BaseStatusTranslator(Class<C> clientClass, Class<P> persistentClass)
    {
        super(clientClass, persistentClass);
    }

    @Override
    protected void afterConvert(ObjectWrapper<C> clientObjectWrapper,
                                ObjectWrapper<P> persistentObjectWrapper, PersistenceContext context)
    {
        if(persistentObjectWrapper.getWrappedObject().getStatus() == null)
            persistentObjectWrapper.getWrappedObject().setStatus(Status.ACTIVE);

        super.afterConvert(clientObjectWrapper, persistentObjectWrapper, context);
    }
}
