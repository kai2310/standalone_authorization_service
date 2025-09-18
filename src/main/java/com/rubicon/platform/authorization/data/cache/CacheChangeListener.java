package com.rubicon.platform.authorization.data.cache;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.WriteContext;
import com.dottydingo.hyperion.core.persistence.event.EntityChangeEvent;
import com.dottydingo.hyperion.core.persistence.event.EntityChangeListener;
import com.rubicon.platform.authorization.model.data.acm.Account;
import com.rubicon.platform.authorization.service.cache.cluster.DistributedInvalidationBroadcaster;

public class CacheChangeListener implements EntityChangeListener<ApiObject<Long>>
{

    @Override
    public void processEntityChange(EntityChangeEvent<ApiObject<Long>> event)
    {
        PersistenceContext context = event.getPersistenceContext();
        switch (context.getHttpMethod())
        {
            case POST:
            {
                if(context.getWriteContext() == WriteContext.create)
                    DistributedInvalidationBroadcaster.getInstance().processCreate(context.getEntity(),
                            event.getUpdatedItem().getId());
                else
                    DistributedInvalidationBroadcaster.getInstance().processUpdate(context.getEntity(),
                            event.getUpdatedItem().getId());
                break;
            }
            case PUT:
            {
                // as we allow people to update the account object through the data service, which can set the status
                // of accounts from 'deleted' to 'active' we need to determine what action is being taken. If the original
                // original status was deleted and its new status is active, we need to send a create message, instead
                // of an update message to make sure this account is added to the cache.
                if (determineUpdateAction(event))
                {
                    DistributedInvalidationBroadcaster.getInstance().processUpdate(context.getEntity(),
                            event.getUpdatedItem().getId());
                }
                else
                {
                    DistributedInvalidationBroadcaster.getInstance().processCreate(context.getEntity(),
                            event.getUpdatedItem().getId());
                }
                break;
            }
            case DELETE:
            {
                DistributedInvalidationBroadcaster.getInstance().processDelete(context.getEntity(),
                        event.getOriginalItem().getId());
                break;
            }
        }
    }


    private boolean determineUpdateAction(EntityChangeEvent<ApiObject<Long>> event)
    {
        boolean sendUpdate = true;
        if ((event.getOriginalItem() instanceof Account))
        {
            Account original = (Account) event.getOriginalItem();
            Account update = (Account) event.getUpdatedItem();

            // if the status goes from deleted to no deleted, we need to make sure the data is added to the cache
            if (original.getStatus().equals("deleted") && update.getStatus().equals("active"))
            {
                sendUpdate = false;
            }
        }

        return sendUpdate;
    }

}
