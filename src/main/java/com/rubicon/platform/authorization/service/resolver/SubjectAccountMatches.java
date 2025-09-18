package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * User: mhellkamp
 * Date: 9/13/12
 */
public class SubjectAccountMatches
{

	private Set<String> scope = new HashSet<String>();
	private Map<CompoundId,AccountProperties> accountPropertiesMap = new HashMap<CompoundId, AccountProperties>();

	public SubjectAccountMatches()
	{
	}

	public SubjectAccountMatches(Map<CompoundId, AccountProperties> accountPropertiesMap, Set<String> scope)
	{
		this.scope = scope;
		this.accountPropertiesMap = accountPropertiesMap;
	}

	public SubjectAccountMatches(CompoundId accountId, AccountProperties accountProperties, Set<String> scope)
	{
		this(Collections.singletonMap(accountId,accountProperties),scope);
	}

	public Set<CompoundId> getAssignedAccounts()
	{
		return accountPropertiesMap.keySet();
	}


	public AccountProperties getAccountProperties(CompoundId account)
	{
		return accountPropertiesMap.get(account);
	}

	public Set<String> getScope()
	{
		return scope;
	}

	public boolean isAuthorized()
	{
		return accountPropertiesMap.size() > 0;
	}


}