package com.rubicon.platform.authorization.service.cache;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.model.PersistentObject;
import com.rubicon.platform.authorization.model.data.acm.Status;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 */
public class StatusObjectCacheLoader<C extends ApiObject,P extends PersistentObject> extends ObjectCacheLoader<C,P,Long>
{
    @Override
    protected Predicate getLoadPredicate(Root root, CriteriaQuery<P> cq,
                                         CriteriaBuilder criteriaBuilder)
    {
        return criteriaBuilder.equal(root.get("status"), Status.ACTIVE);
    }
}
