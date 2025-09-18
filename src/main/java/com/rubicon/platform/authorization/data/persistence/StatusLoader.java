package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.BaseStatusPersistentObject;
import com.rubicon.platform.authorization.model.data.acm.Status;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 */
public abstract class StatusLoader<T extends BaseStatusPersistentObject> extends AbstractLoader<T,Long>
{
    @Override
    protected Predicate buildExistsPredicate(Long id, CriteriaBuilder cb, Root root)
    {
        return cb.and(super.buildExistsPredicate(id, cb, root), cb.equal(root.get("status"), Status.ACTIVE));
    }
}
