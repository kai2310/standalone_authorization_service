package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.model.data.acm.Account;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 */
public class AccountObjectCacheLoader extends ObjectCacheLoader<Account,PersistentAccount,Long>
{
    @Override
    protected Predicate getLoadPredicate(Root root, CriteriaQuery<PersistentAccount> cq,
                                         CriteriaBuilder criteriaBuilder)
    {
        return criteriaBuilder.notEqual(root.get("status"), "deleted");
    }
}
