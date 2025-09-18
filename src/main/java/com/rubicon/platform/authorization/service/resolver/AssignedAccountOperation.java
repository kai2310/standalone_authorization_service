package com.rubicon.platform.authorization.service.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: mhellkamp
 * Date: 11/30/12
 */
public class AssignedAccountOperation
{
	private static Logger logger = LoggerFactory.getLogger(AssignedAccountOperation.class);

	private Set<String> allowedProperties = new HashSet<String>();
	private Set<String> deniedProperties = new HashSet<String>();
	private AccountProperties roleAssignedProperties = new AccountProperties();
	private OperationMatch denyMatch = OperationMatch.none;
	private OperationMatch allowMatch = OperationMatch.none;


	public boolean isAuthorized()
	{
		logger.debug("allowMatch:{} denyMatch:{}",allowMatch,denyMatch);
		return allowMatch.hasPrecedence(denyMatch);
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

	public void setRoleAssignedProperties(AccountProperties roleAssignedProperties)
	{
		this.roleAssignedProperties = roleAssignedProperties;
	}

	public void setDenyMatch(OperationMatch match)
	{
		if(match.hasPrecedence(denyMatch))
			denyMatch = match;
	}

	public void setAllowMatch(OperationMatch match)
	{
		if(match.hasPrecedence(allowMatch))
			allowMatch = match;
	}

	public boolean isExplicitlyDenied()
	{
		return denyMatch == OperationMatch.action;
	}

	public Set<String> resolveAllowedProperties()
	{
		logger.debug("Resolving allowed properties...");

		Set<String> roleAllowed = roleAssignedProperties.getAllowedProperties();
		Set<String> roleDenied = roleAssignedProperties.getDeniedProperties();

		boolean hasRoleAllowed = !roleAllowed.isEmpty();
		boolean hasRoleDenied = !roleDenied.isEmpty();
		boolean hasAccountAllowed = !allowedProperties.isEmpty();
		boolean hasAccountDenied = !deniedProperties.isEmpty();

		Set<String> resolved = new HashSet<String>();
		if(hasRoleAllowed)
		{
			logger.debug("Adding allowed properties set in role: {}",roleAllowed);

			resolved.addAll(roleAllowed);

			if(hasRoleDenied)
			{
				logger.debug("Removing denied properties set in role: {}",roleDenied);
				resolved.removeAll(roleDenied);
			}

			if(hasAccountAllowed)
			{
				logger.debug("Retaining allowed properties set in account: {}",allowedProperties);
				resolved.retainAll(allowedProperties);
			}

			if(hasAccountDenied)
			{
				logger.debug("Removing denied properties set in account: {}",deniedProperties);
				resolved.removeAll(deniedProperties);
			}

		}
		else if(hasAccountAllowed)
		{
			logger.debug("Adding allowed properties set in account: {}",allowedProperties);
			resolved.addAll(allowedProperties);


			if(hasRoleDenied)
			{
				logger.debug("Removing denied properties set in role: {}",roleDenied);
				resolved.removeAll(roleDenied);
			}

			if(hasAccountDenied)
			{
				logger.debug("Removing denied properties set in account: {}",deniedProperties);
				resolved.removeAll(deniedProperties);
			}
		}
		else
		{
			logger.debug("No allowed properties set.");
			return null;
		}

	    logger.debug("Final resolved properties: {}",resolved);
		return resolved;

	}

	public Set<String> resolveDeniedProperties()
	{
		logger.debug("Resolving denied properties...");

		Set<String> roleAllowed = roleAssignedProperties.getAllowedProperties();
		Set<String> roleDenied = roleAssignedProperties.getDeniedProperties();

		boolean hasRoleAllowed = !roleAllowed.isEmpty();
		boolean hasRoleDenied = !roleDenied.isEmpty();
		boolean hasAccountAllowed = !allowedProperties.isEmpty();
		boolean hasAccountDenied = !deniedProperties.isEmpty();

		if(hasRoleAllowed || hasAccountAllowed)
		{
			logger.debug("Has role or account allowed properties specified, no denied properties will be returned.");
			return null;
		}

		if(!hasRoleDenied && !hasAccountDenied)
		{
			logger.debug("No denied properties set, nothing to return.");
			return null;
		}

		Set<String> resolved = new HashSet<String>();

		if(hasRoleDenied)
		{
			logger.debug("Adding denied properties set in role: {}",roleDenied);
			resolved.addAll(roleDenied);
		}

		if(hasAccountDenied)
		{
			logger.debug("Adding denied properties set in account: {}",deniedProperties);
			resolved.addAll(deniedProperties);
		}

		logger.debug("Final resolved properties: {}",resolved);
		return resolved;
	}

    public void add(AssignedAccountOperation other)
    {
        addAllowedProperties(other.allowedProperties);
        addDeniedProperties(other.deniedProperties);
        roleAssignedProperties.add(other.roleAssignedProperties);
        setAllowMatch(other.allowMatch);
        setDenyMatch(other.denyMatch);
    }
}
