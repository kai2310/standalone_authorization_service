package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.Status;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Component
public class RoleAssignmentLoader extends AbstractLoader<PersistentRoleAssignment, Long>
        implements RoleAssignmentUniqueCheck
{
    public static final String ROLE_ASSIGNMENT_ID_BY_ROLE_ID_QUERY =
            "SELECT ra.role_assignment_id FROM role_assignments ra WHERE ra.role_id = :id and status = 'ACTIVE'";


    @Override
    protected Class<PersistentRoleAssignment> getEntityClass()
    {
        return PersistentRoleAssignment.class;
    }


    @Override
    public boolean exists(CompoundId subjectId, CompoundId account, Long roleId, Long accountGroupId)
    {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<PersistentRoleAssignment> root = criteriaQuery.from(PersistentRoleAssignment.class);
        criteriaQuery.select(cb.count(root));

        Predicate[] predicates =
                RoleAssignmentKeyPredicateBuilder.buildPredicates(cb, root, subjectId, account, roleId, accountGroupId,
                        true);

        criteriaQuery.where(predicates);

        Long total = em.createQuery(criteriaQuery).getSingleResult();

        return total > 0;

    }

    public List<CompoundId> findSubjects(CompoundId account, Long accountGroupId, Long roleId )
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CompoundId> criteriaQuery = cb.createQuery(CompoundId.class);
        Root<PersistentRoleAssignment> root = criteriaQuery.from(getEntityClass());

        criteriaQuery.select(root.<CompoundId>get("subject"));
        criteriaQuery.distinct(true);

        criteriaQuery.orderBy(cb.asc(root.get("subject").get("idType")), cb.asc(root.get("subjectNumeric")));

        List<Predicate> predicates = new ArrayList<>();
        if (account != null)
        {

            predicates.add(cb.and(
                    cb.equal(root.get("account").get("idType"), account.getIdType()),
                    cb.equal(root.get("account").get("id"), account.getId())
            ));
        }

        if (accountGroupId != null)
        {
            predicates.add(cb.equal(root.get("accountGroupId"), accountGroupId));
        }

        if (roleId != null)
        {
            predicates.add(cb.equal(root.get("roleId"), roleId));
        }

        predicates.add(cb.equal(root.get("status"), Status.ACTIVE));
        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));

        return em.createQuery(criteriaQuery).getResultList();
    }


    public List<Long> getRoleAssignmentByRoleId(Long roleId)
    {
        Query query = em.createNativeQuery(ROLE_ASSIGNMENT_ID_BY_ROLE_ID_QUERY);
        query.setParameter("id", roleId);

        return query.getResultList();
    }

}
