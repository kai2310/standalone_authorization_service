package com.rubicon.platform.authorization.test;

import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.model.api.acm.AuthorizeRequest;
import com.rubicon.platform.authorization.model.api.acm.AuthorizeResponse;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * User: mhellkamp
 * Date: 2/15/13
 */
public class SmokeAuthClient
{
	private RestTemplate restTemplate = new RestTemplate();
	private String serverUrl;


	public SmokeAuthClient(String serverUrl)
	{
		this.serverUrl = serverUrl;
	}

	public AuthorizeResponse authorize(Set<String> subjects,String accountContext, String service,String resource,String action)
	{
		AuthorizeRequest request = new AuthorizeRequest();
		request.setSubjects(subjects);
		request.setAccountContext(accountContext);
		request.setService(service);
		request.setResource(resource);
		request.setAction(action);
		return authorize(request);
	}
	public AuthorizeResponse authorize(String subject,String accountContext, String service,String resource,String action)
	{
		return authorize(Util.asSet(subject),accountContext,service,resource,action);
	}

	public AuthorizeResponse authorize(String subject,String service,String resource,String action)
	{
		return authorize(subject,null,service,resource,action);
	}

	public AuthorizeResponse authorize(AuthorizeRequest request)
	{
		return restTemplate.postForObject(serverUrl + "authorize",request,AuthorizeResponse.class);
	}
}
