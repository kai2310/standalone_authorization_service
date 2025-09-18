package com.rubicon.platform.authorization.service.resolver;

import java.util.*;

/**
 * User: mhellkamp
 * Date: 11/29/12
 */
public class AccountProperties
{
	private Set<String> allowedProperties = new HashSet<String>();
	private Set<String> deniedProperties = new HashSet<String>();

	public AccountProperties()
	{
	}

	public AccountProperties(Set<String> allowedProperties, Set<String> deniedProperties)
	{
		this.allowedProperties.addAll(allowedProperties);
		this.deniedProperties.addAll(deniedProperties);
	}

	public void addAllowedProperties(Collection<String> properties)
	{
		if(properties != null)
			this.allowedProperties.addAll(properties);
	}

	public void addDeniedProperties(Collection<String> properties)
	{
		if(properties != null)
			this.deniedProperties.addAll(properties);
	}

	public Set<String> getAllowedProperties()
	{
		return allowedProperties;
	}

	public Set<String> getDeniedProperties()
	{
		return deniedProperties;
	}

    public void add(AccountProperties other)
    {
        addAllowedProperties(other.allowedProperties);
        addDeniedProperties(other.deniedProperties);
    }
}
