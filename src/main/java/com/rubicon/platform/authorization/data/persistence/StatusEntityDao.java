package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.jpa.persistence.JpaDao;
import com.rubicon.platform.authorization.data.model.StatusEntity;
import com.rubicon.platform.authorization.model.data.acm.Status;

/**
 */
public class StatusEntityDao extends JpaDao<StatusEntity,Long>
{
    @Override
    public void delete(StatusEntity entity)
    {
        entity.setStatus(Status.DELETED);
        em.merge(entity);
    }
}
