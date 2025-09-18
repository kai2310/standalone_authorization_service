package com.rubicon.platform.authorization.service.cache;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.model.PersistentObject;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 * User: mhellkamp
 * Date: 10/17/12
 */
public class ObjectCacheLoader<C extends ApiObject,P extends PersistentObject,ID extends Serializable>
		extends AbstractCacheLoader<C,P>  implements CacheNotificationListener<ID>
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String endpointName;
	private Class<P> entityClass;

	@Override
	public String getEndpointName()
	{
		return endpointName;
	}

	public void setEndpointName(String endpointName)
	{
		this.endpointName = endpointName;
	}

	public void setEntityClass(Class<P> entityClass)
	{
		this.entityClass = entityClass;
	}

	@Override
	protected void loadCache(EntityManager entityManager)
	{
		logger.info("Loading {} cache.", endpointName);

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<P> cq = criteriaBuilder.createQuery(entityClass);
		Root root = cq.from(entityClass);

		Predicate predicate = getLoadPredicate(root,cq,criteriaBuilder);
		if(predicate != null)
			cq.where(predicate);

		TypedQuery<P> query = entityManager.createQuery(cq);
		List<P> list = query.getResultList();

		Translator<C,P> translator = getTranslator();
		PersistenceContext requestContext = buildPersistenceContext();
		for (P item : list)
		{
			C client = translator.convertPersistent(item, requestContext);
			cache.addEntry(client);
		}

		logger.info("Cache {} - loaded {} entries.",endpointName,list.size());
	}

	protected Predicate getLoadPredicate(Root root, CriteriaQuery<P> cq, CriteriaBuilder criteriaBuilder)
	{
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public void onCreate(ID id)
	{
		C item = loadItem(entityClass,id);
		if(item != null)
			cache.addEntry(item);
		else
			logger.warn("No item found for {} id:{}",entityClass.getName(),id);
	}

	@Override
	@Transactional(readOnly = true)
	public void onUpdate(ID id)
	{
		C item = loadItem(entityClass,id);
		if(item != null)
			cache.updateEntry(item);
		else
			logger.warn("No item found for {} id:{}", entityClass.getName(), id);
	}

	@Override
	@Transactional(readOnly = true)
	public void onDelete(ID id)
	{
		cache.removeEntry(id);
	}
}
