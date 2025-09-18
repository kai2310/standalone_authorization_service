package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.translator.BaseRoleAssignmentTranslator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 */
public class RoleAssignmentKeyPredicateBuilder
{
    protected static Predicate[] buildPredicates(CriteriaBuilder cb, Root<PersistentRoleAssignment> root,
                                                 CompoundId subjectId, CompoundId account,
                                                 Long roleId, Long accountGroupId, boolean activeOnly)
    {
        int items = activeOnly ? 5 : 4;
        Predicate[] predicates = new Predicate[items];
        predicates[0] = cb.equal(root.get("roleId"),roleId);
        predicates[1] = getIdPredicate(subjectId, "subject", cb, root);
        if(account != null)
        {
            predicates[2] = getIdPredicate(account,"account",cb,root);
            predicates[3] = cb.equal(root.get("accountGroupId"), BaseRoleAssignmentTranslator.EMPTY_ACCOUNT_GROUP);
        }
        else
        {
            predicates[2] = cb.equal(root.get("accountGroupId"),accountGroupId);
            predicates[3] =  getIdPredicate(BaseRoleAssignmentTranslator.EMPTY_ACCOUNT, "account", cb, root);
        }

        if(activeOnly)
            predicates[4]= cb.equal(root.get("active"), 1);

        return predicates;
    }

    private static Predicate getIdPredicate(CompoundId id, String field, CriteriaBuilder cb, Root<PersistentRoleAssignment> root)
    {
        return cb.and(
                cb.equal(root.get(field).get("idType"), id.getIdType()),
                cb.equal(root.get(field).get("id"), id.getId())
        );
    }
}
