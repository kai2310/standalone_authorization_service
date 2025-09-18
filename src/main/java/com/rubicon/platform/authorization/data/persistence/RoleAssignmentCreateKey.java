package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.core.persistence.CreateKeyProcessor;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.BaseRoleAssignment;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 */
public class RoleAssignmentCreateKey implements CreateKeyProcessor<BaseRoleAssignment,Long>
{
    @javax.persistence.PersistenceContext
    private EntityManager em;

    @Override
    public Long lookup(BaseRoleAssignment item, PersistenceContext persistenceContext)
    {
        boolean hasAccount = item.getAccount() != null;
        boolean hasAccountGroupId = item.getAccountGroupId() != null;

        // don't want both
        if(hasAccount && hasAccountGroupId)
            return null;

        if(item.getSubject() == null || item.getRoleId() == null || (!hasAccount && !hasAccountGroupId))
            return null;

        CompoundId subject = CompoundId.build(item.getSubject());
        if(subject == null)
            return null;

        CompoundId account = null;
        if(hasAccount)
        {
            account = CompoundId.build(item.getAccount());
            if (account == null)
                return null;
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = cb.createQuery(Long.class);
        Root<PersistentRoleAssignment> root = criteriaQuery.from(PersistentRoleAssignment.class);
        criteriaQuery.select(root.get("id"));

        Predicate[] predicates = RoleAssignmentKeyPredicateBuilder.buildPredicates(cb, root, subject, account,
                item.getRoleId(), item.getAccountGroupId(), true);

        criteriaQuery.where(predicates);

        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        List<Long> results = query.getResultList();
        if(results.size() == 1)
            return results.get(0);

        return null;
    }


}
