package com.rubicon.platform.authorization.service.cache;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.endpoint.pipeline.auth.NoOpAuthorizationContext;
import com.dottydingo.hyperion.core.model.PersistentObject;
import com.dottydingo.hyperion.core.registry.EntityPlugin;
import com.dottydingo.hyperion.core.registry.ServiceRegistry;
import com.dottydingo.hyperion.core.translation.Translator;
import com.dottydingo.service.endpoint.context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;


/**
 * User: mhellkamp
 * Date: 10/17/12
 */
public abstract class AbstractCacheLoader<C extends ApiObject,P extends PersistentObject>
        implements RefreshableCache
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private ServiceRegistry serviceRegistry;
	private Translator<C, P> translator;
	protected WritableCache<C,Serializable> cache;

	@PersistenceUnit
	private EntityManagerFactory emf;

	@PersistenceContext
	private EntityManager entityManager;


	public void setServiceRegistry(ServiceRegistry serviceRegistry)
	{
		this.serviceRegistry = serviceRegistry;
	}

	public void setCache(WritableCache<C,Serializable> cache)
	{
		this.cache = cache;
	}

	public void init()
	{
		EntityPlugin plugin = serviceRegistry.getPluginForName(getEndpointName());
		if(plugin == null)
			throw new RuntimeException(String.format("Could not load plugin for %s",getEndpointName()));

		// this will load the latest version of the translator
		translator = plugin.getApiVersionRegistry().getPluginForVersion(null).getTranslator();

		EntityManager em = emf.createEntityManager();
		try
		{
			loadCache(em);
		}
		finally
		{
			if(em != null)
				em.close();
		}
	}

    @Override
    public void refresh()
    {
        cache.clear();
        init();
    }

	protected Translator<C, P> getTranslator()
	{
		return translator;
	}

	protected C loadItem(Class<P> entityClass, Serializable id)
	{
		try
		{
			P persistent = entityManager.find(entityClass,id);
			if(persistent != null)
			{
				Translator<C,P> translator = getTranslator();
                return translator.convertPersistent(persistent,buildPersistenceContext());
			}
			else
				logger.warn("Could not find {} id={}",entityClass.getName(),id);
		}
        catch (PersistenceException e)
        {
            throw new CacheLoadException(String.format("Error loading %s id=%s",entityClass.getName(),id),e);
        }
		catch (Exception e)
		{
			logger.error(String.format("Error loading %s id=%s",entityClass.getName(),id),e);
		}

		return null;
	}

    protected abstract void loadCache(EntityManager entityManager);

    protected com.dottydingo.hyperion.core.persistence.PersistenceContext buildPersistenceContext()
    {
        com.dottydingo.hyperion.core.persistence.PersistenceContext
                context = new com.dottydingo.hyperion.core.persistence.PersistenceContext();

        context.setUserContext(new UserContext());
        context.setAuthorizationContext(new NoOpAuthorizationContext());
        return context;
    }

}
