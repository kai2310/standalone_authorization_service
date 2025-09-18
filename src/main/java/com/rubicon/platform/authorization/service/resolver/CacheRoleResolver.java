package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.model.data.acm.Role;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;
import com.rubicon.platform.authorization.service.cache.ServiceOperation;
import com.rubicon.platform.authorization.service.cache.ServiceOperationsCache;
import com.rubicon.platform.authorization.service.cache.ServiceOperationsHolder;
import com.rubicon.platform.authorization.service.cache.ServiceRoleAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * User: mhellkamp
 * Date: 10/17/12
 */
@Component("cacheRoleResolver")
public class CacheRoleResolver implements RoleResolver
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private OperationMatcher operationMatcher = new OperationMatcher();

	@Autowired
	private RoleAssignmentResolver roleAssignmentResolver;

	@Autowired
	@Qualifier("roleObjectCache")
	private ServiceOperationsCache<Role> roleCache;

	@Override
	public ResolvedSubjectAccounts resolveOperation(List<CompoundId> subjectIds, CompoundId accountId, String service,
											  String endpoint, String action)
	{
		List<ServiceRoleAssignment> roleAssignments = roleAssignmentResolver.resolveRoleAssignments(subjectIds,
				accountId);
		if(logger.isDebugEnabled())
			logger.debug(String.format("Found %d roleAssignments.", roleAssignments.size()));

		return getResolvedOperation(roleAssignments, accountId, service, endpoint, action);
	}


	protected ResolvedSubjectAccounts getResolvedOperation(List<ServiceRoleAssignment> roleAssignments,
												   CompoundId accountContext,
												   String service,
												   String resource,
												   String action)
	{

		ResolvedSubjectAccounts match = new ResolvedSubjectAccounts();
		logger.debug("Checking assigned Roles for matching operations.");

		for (ServiceRoleAssignment roleAssignment : roleAssignments)
		{

			ServiceOperationsHolder<Role> roleHolder =
					roleCache.getServiceOperationsHolder(roleAssignment.getRoleId());
			if (roleHolder == null)
			{
				logger.warn(String.format("Missing role for id=%d", roleAssignment.getRoleId()));
				continue;
			}

            OperationMatchResult operationMatchResult =
                    checkRole(service, resource, action, roleHolder, roleAssignment);
			if(roleAssignment.getAccountId() != null)
				match.addAccountMatch(roleAssignment.getAccountId(), operationMatchResult);
			else
				match.addAccountGroupMatch(roleAssignment.getAccountGroupId(), operationMatchResult);

		}

		return match;
	}

	protected OperationMatchResult checkRole(String service, String resource, String action,
											 ServiceOperationsHolder<Role> roleHolder, RoleAssignment roleAssignment)
	{

		OperationMatchResult operationMatchResult = new OperationMatchResult();
        Role role = roleHolder.getWrapped();

		if(logger.isDebugEnabled())
			logger.debug("Checking operations for Role {}-{}.",role.getId(),role.getLabel());

		// first check denied
        List<ServiceOperation> deniedOperations = roleHolder.getDeniedOperations(service);

        for (ServiceOperation operation : deniedOperations)
		{
			OperationMatch operationMatch = operationMatcher.matchOperation(operation,
					resource, action);
			if(!(operationMatch == OperationMatch.none))
			{
				if(isSet(operation.getProperties()))
				{
					operationMatchResult.addDeniedProperties(operation.getProperties());
				}
				else
				{
					operationMatchResult.setDenyMatch(operationMatch);

					// if we have an explicit deny at the action level then we're done.
					if(operationMatch == OperationMatch.action)
					{
						logger.debug("Operation has been explicitly denied. Stopping evaluation.");
						return operationMatchResult;
					}
					logger.debug("Operation has been denied with a wildcard. Continuing to evaluate.");
				}
			}
		}

        // now check allowed
        List<ServiceOperation> allowedOperations = roleHolder.getAllowedOperations(service);

        for (ServiceOperation operation : allowedOperations)
		{
			OperationMatch operationMatch = operationMatcher.matchOperation(operation,
                    resource, action);
			if(!(operationMatch == OperationMatch.none))
			{
				operationMatchResult.setAllowMatch(operationMatch);
				operationMatchResult.addAllowedProperties(operation.getProperties());
				operationMatchResult.addScope(roleAssignment.getScope());
			}
		}

		if(logger.isDebugEnabled())
			logger.debug("Allow match: {}, deny match: {}, allowed properties: {}, denied properties: {}",
					operationMatchResult.getAllowMatch(),operationMatchResult.getDenyMatch(),
					operationMatchResult.getAllowedProperties(),operationMatchResult.getDeniedProperties());

		return operationMatchResult;
	}


	private boolean isSet(Collection collection)
	{
		return collection != null && !collection.isEmpty();
	}
}
