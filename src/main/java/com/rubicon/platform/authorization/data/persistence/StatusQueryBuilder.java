package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.jpa.persistence.query.JpaPersistentQueryBuilder;
import com.rubicon.platform.authorization.model.data.acm.Status;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
*/
public class StatusQueryBuilder implements JpaPersistentQueryBuilder
{
    private final Status status;

    public StatusQueryBuilder(Status status)
    {
        this.status = status;
    }

    @Override
    public Predicate buildPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb)
    {
        return cb.equal(root.get("status"), status);
    }
}
