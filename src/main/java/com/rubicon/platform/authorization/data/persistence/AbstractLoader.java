package com.rubicon.platform.authorization.data.persistence;

import com.rubicon.platform.authorization.model.data.acm.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 */
public abstract class AbstractLoader<T,ID extends Serializable>
{
    protected Logger logger = LoggerFactory.getLogger(AbstractLoader.class);
    @PersistenceContext
    protected javax.persistence.EntityManager em;


    public boolean exists(ID id)
    {
        try
        {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root countRoot = countQuery.from(getEntityClass());

            countQuery.select(cb.count(countRoot));
            countQuery.where(buildExistsPredicate(id, cb, countRoot));

            Long total = em.createQuery(countQuery).getSingleResult();

            return total > 0;
        }
        catch (Exception e)
        {
            logger.error(String.format("Error looking up %s for id: %s",getEntityClass().getSimpleName(),id),e);
        }

        return false;
    }

    protected Predicate buildExistsPredicate(ID id, CriteriaBuilder cb, Root countRoot)
    {
        return cb.equal(countRoot.get("id"),id);
    }

    public T find(Long id)
    {
        try
        {
            return em.find(getEntityClass(),id);
        }
        catch (Exception e)
        {
            logger.error(String.format("Error looking up %s for id: %s",getEntityClass().getSimpleName(),id),e);
        }

        return null;
    }

    public List<Long> findIds(Collection<Long> next)
    {
        if(next.isEmpty())
            return Collections.emptyList();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root root = query.from(getEntityClass());

        Path<Long> id = root.get("id");

        query.where(id.in(next));
        query.select(root.get("id"));

        return em.createQuery(query).getResultList();
    }

    public boolean isLabelUnique(String label)
    {
        return isLabelUnique(label,null);
    }

    public boolean isLabelUnique(String label, ID id )
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root countRoot = countQuery.from(getEntityClass());

        countQuery.select(cb.count(countRoot));

        Predicate labelPredicate = cb.equal(countRoot.get("label"),label);

        if(id != null)
            countQuery.where(labelPredicate,
                    cb.equal(countRoot.get("status"), Status.ACTIVE),
                    cb.notEqual(countRoot.get("id"),id));
        else
            countQuery.where(labelPredicate,cb.equal(countRoot.get("status"), Status.ACTIVE));

        Long total = em.createQuery(countQuery).getSingleResult();

        return total == 0;
    }

    protected boolean hasReferences(String queryString, Long id)
    {
        Query query = em.createNativeQuery(queryString);
        query.setParameter("id",id);

        return hasResults(query.getSingleResult());
    }

    private boolean hasResults(Object value)
    {
        if(value instanceof Number)
        {
            Number count = (Number) value;
            return count.longValue() > 0;
        }

        return false;
    }

    protected abstract Class<T> getEntityClass();
}
