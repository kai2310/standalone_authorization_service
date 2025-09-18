package com.rubicon.platform.authorization.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * User: mhellkamp
 * Date: 1/31/13
 */
@Component
public class ConfigurationManager
{
	private Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

	@PersistenceContext
	private EntityManager em;

	public String getValue(String key)
	{
		Configuration configuration = em.find(Configuration.class, key);
		if(configuration != null)
			return configuration.getValue();

		return null;
	}

	@Transactional
	public void setValue(String key,String value)
	{
		Configuration configuration = em.find(Configuration.class,key);
		if(configuration == null)
		{
			configuration = new Configuration(key,value);
			try
			{
				em.persist(configuration);
				return;
			}
			catch(Exception e)
			{
				logger.info("Could not create new config option, trying to update.",e);
			}
		}

		configuration.setValue(value);
		try
		{
			em.merge(configuration);
		}
		catch (Exception e)
		{
			throw new RuntimeException(
					String.format("Could not update configuration key:\"%s\" value:\"%s\"",key,value),e);
		}

	}

}
