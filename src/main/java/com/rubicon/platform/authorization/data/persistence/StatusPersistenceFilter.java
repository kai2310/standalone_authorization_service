package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.core.persistence.EmptyPersistenceFilter;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.query.PersistentQueryBuilder;
import com.rubicon.platform.authorization.data.model.StatusEntity;
import com.rubicon.platform.authorization.model.data.acm.Status;

/**
 */
public class StatusPersistenceFilter extends EmptyPersistenceFilter<StatusEntity>
{
    private final PersistentQueryBuilder queryBuilder = new StatusQueryBuilder(Status.ACTIVE);


    @Override
    public PersistentQueryBuilder getFilterQueryBuilder(PersistenceContext persistenceContext)
    {
        if(!deletedVisible(persistenceContext))
            return queryBuilder;

        return null;
    }

    protected boolean deletedVisible(PersistenceContext persistenceContext)
    {
        return persistenceContext.getAdditionalParameters().getFirst("showDeleted") != null;
    }

    @Override
    public boolean isVisible(StatusEntity persistentObject, PersistenceContext persistenceContext)
    {
        return  deletedVisible(persistenceContext) ||
                (!deletedVisible(persistenceContext) && persistentObject.getStatus() != Status.DELETED);
    }

    @Override
    public boolean canUpdate(StatusEntity persistentObject, PersistenceContext persistenceContext)
    {
        return persistentObject.getStatus() != Status.DELETED;
    }

    @Override
    public boolean canDelete(StatusEntity persistentObject, PersistenceContext persistenceContext)
    {
        return persistentObject.getStatus() != Status.DELETED;
    }



}
