package com.rubicon.platform.authorization.test;

import com.dottydingo.hyperion.api.ApiObject;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: mhellkamp
 * Date: 2/15/13
 */
public class SmokeDataClient<T extends ApiObject<Long>>
{
	private RestTemplate restTemplate = new RestTemplate();
	private String serverUrl;
	private Class<T> type;
	private Map<Long,T> entityMap = new HashMap<Long,T>();

	public SmokeDataClient(String serverUrl, Class<T> type)
	{
		this.serverUrl = serverUrl;
		this.type = type;
	}

	public T create(T item)
	{
		T created = restTemplate.postForObject(serverUrl, item, type);
		entityMap.put(created.getId(),created);
		return created;
	}

	public void delete(Long id)
	{
		restTemplate.delete(serverUrl + "/{id}",id.toString());
		entityMap.remove(id);
	}

	public void cleanup()
	{
		List<Long> ids = new ArrayList<Long>(entityMap.keySet());
		for (Long id : ids)
		{
			try
			{
				delete(id);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
}
