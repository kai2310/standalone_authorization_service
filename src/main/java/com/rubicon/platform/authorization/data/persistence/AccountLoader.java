package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import org.springframework.stereotype.Component;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 *
 */
@Component
public class AccountLoader extends AbstractLoader<PersistentAccount, Long> implements AccountUniqueCheck
{
    public static final String ACCOUNT_ID_BY_FEATURE_QUERY =
            "SELECT a.account_id FROM accounts a JOIN account_feature_roles afr USING (account_id) WHERE afr.feature_role_id = :id";


    @Override
    protected Class<PersistentAccount> getEntityClass()
    {
        return PersistentAccount.class;
    }

    @Override
    public boolean exists(String accountType, String accountId)
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<PersistentAccount> root = query.from(PersistentAccount.class);
        query.select(cb.count(root));

        query.where(cb.equal(root.get("accountId").get("idType"), accountType),
                cb.equal(root.get("accountId").get("id"), accountId));

        long result = em.createQuery(query).getSingleResult();

        return result > 0;
    }

    public boolean exists(CompoundId accountId)
    {
        return exists(accountId.getIdType(), accountId.getId());
    }

    public PersistentAccount findByAccountId(CompoundId accountId)
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PersistentAccount> query = cb.createQuery(PersistentAccount.class);
        Root<PersistentAccount> root = query.from(PersistentAccount.class);

        query.where(cb.equal(root.get("accountId").get("idType"), accountId.getIdType()),
                cb.equal(root.get("accountId").get("id"), accountId.getId()));

        List<PersistentAccount> result = em.createQuery(query).getResultList();
        if (result.size() == 1)
        {
            return result.get(0);
        }

        return null;
    }

    public PersistentAccount save(PersistentAccount persistentAccount)
    {
        if (persistentAccount.getId() == null)
        {
            em.persist(persistentAccount);
            return persistentAccount;
        }
        return em.merge(persistentAccount);
    }

    public List<Long> getAccountByFeatureId(Long featureId)
    {
        Query query = em.createNativeQuery(ACCOUNT_ID_BY_FEATURE_QUERY);
        query.setParameter("id", featureId);

        return query.getResultList();
    }
}
