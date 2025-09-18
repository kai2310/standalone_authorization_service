package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.model.data.acm.Account;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.service.cache.AccountObjectCache;
import com.rubicon.platform.authorization.service.cache.ServiceOperation;
import com.rubicon.platform.authorization.service.cache.ServiceOperationsCache;
import com.rubicon.platform.authorization.service.cache.ServiceOperationsHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: mhellkamp
 * Date: 11/29/12
 */
@Component("cacheAccountFeatureResolver")
public class CacheAccountFeatureResolver implements AccountFeatureResolver
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private OperationMatcher operationMatcher = new OperationMatcher();

	@Autowired
	@Qualifier("accountObjectCache")
	private AccountObjectCache accountCache;
	@Autowired
	@Qualifier("accountFeatureObjectCache")
	private ServiceOperationsCache<AccountFeature> accountFeatureCache;

	@Override
	public ResolvedOperationAccounts resolveOperation(String service, String resource,
													  String action, SubjectAccountMatches assignedRoleOperation)
	{
		ResolvedOperationAccounts match = new ResolvedOperationAccounts();

        Set<CompoundId> assignedAccounts = assignedRoleOperation.getAssignedAccounts();

        logger.debug("Checking AccountFeatures.");

        // multi account optimization
        if(assignedAccounts.size() > 1)
        {

            logger.debug("Found {} matching accounts for {}",assignedAccounts.size());

            List<ServiceOperationsHolder<AccountFeature>> matchingFeatures = getMatchingFeatures(service, resource, action);

            logger.debug("Found {} matching features",matchingFeatures.size());

            for (ServiceOperationsHolder<AccountFeature> feature : matchingFeatures)
            {
                AssignedAccountOperation featureMatch = new AssignedAccountOperation();
                checkAccountFeature(featureMatch,service,resource,action,feature);

                Collection<CompoundId> hasFeature = accountCache.getForFeatureId(feature.getWrapped().getId());
                hasFeature.retainAll(assignedAccounts);

                logger.debug("Found {} matching accounts for feature ID:{}",hasFeature.size(),feature.getWrapped().getId());
                for (CompoundId account : hasFeature)
                {
                    AssignedAccountOperation assignedAccountOperation = match.getAccountAssignedOperation(account);
                    assignedAccountOperation.setRoleAssignedProperties(assignedRoleOperation.getAccountProperties(account));
                    assignedAccountOperation.add(featureMatch);
                }
            }
        }
        else
        {

            for (CompoundId id : assignedAccounts)
            {
                AccountProperties roleProperties = assignedRoleOperation.getAccountProperties(id);

                AssignedAccountOperation assignedAccountOperation = match.getAccountAssignedOperation(id);
                assignedAccountOperation.setRoleAssignedProperties(roleProperties);

                checkAccount(assignedAccountOperation,id, service, resource, action);
            }

        }



		return match;
	}

	protected void checkAccount(AssignedAccountOperation match, CompoundId accountId,
							  String service,String resource,String action)
	{

		if(logger.isDebugEnabled())
			logger.debug("Checking accountId={}",accountId.asIdString());

		Account account = accountCache.getByAccountId(accountId);
		if(account == null)
		{
			logger.warn("Could not find accountId={}",accountId.asIdString());
			return;
		}

		// only active accounts
		if(!account.getStatus().equals("active"))
		{
			if(logger.isDebugEnabled())
				logger.debug("Account {}-{} is not active",accountId.asIdString(),account.getAccountName());

			return;
		}

		Set<Long> accountFeaturesIds = account.getAccountFeatureIds();

		if(logger.isDebugEnabled())
			logger.debug(String.format("Found %d account features.",accountFeaturesIds.size()));


		for (Long id : accountFeaturesIds)
		{
			{
				ServiceOperationsHolder<AccountFeature>
                        accountFeatureHolder = accountFeatureCache.getServiceOperationsHolder(id);
				if(accountFeatureHolder == null)
				{
					logger.warn("Could not find accountFeature id={}",id);
					continue;
				}
				logger.debug("Checking AccountFeature {}-{}", id, accountFeatureHolder.getWrapped().getLabel());
				checkAccountFeature(match, service, resource, action, accountFeatureHolder);
				if(match.isExplicitlyDenied())
				{
					return;
				}

			}

		}

	}

	protected void checkAccountFeature(AssignedAccountOperation match, String service, String resource,
										  String action, ServiceOperationsHolder<AccountFeature> accountFeatureHolder)
	{
        AccountFeature accountFeature = accountFeatureHolder.getWrapped();

        // first check denied
        List<ServiceOperation> deniedOperations = accountFeatureHolder.getDeniedOperations(service);

		if(logger.isDebugEnabled())
		{
			if(deniedOperations.size() > 0)
				logger.debug("Checking denied operations for AccountFeature {}-{}.",accountFeature.getId(),accountFeature.getLabel());
			else
				logger.debug("No denied operations for AccountFeature {}-{}.",accountFeature.getId(),accountFeature.getLabel());
		}


        for (ServiceOperation operation : deniedOperations)
		{
			OperationMatch operationMatch = operationMatcher.matchOperation(operation, resource, action);
			if(!(operationMatch == OperationMatch.none))
			{
				if(isSet(operation.getProperties()))
				{
					match.addDeniedProperties(operation.getProperties());
				}
				else
				{
					match.setDenyMatch(operationMatch);

					// if we have an explicit deny at the action level then we're done.
					if(operationMatch == OperationMatch.action)
					{
						logger.debug("Operation has been explicitly denied. Stopping evaluation.");
						return;
					}
					logger.debug("Operation has been denied with a wildcard. Continuing to evaluate.");
				}
			}
		}

        // now check allowed
        List<ServiceOperation> allowedOperations = accountFeatureHolder.getAllowedOperations(service);

		if(logger.isDebugEnabled())
		{
			if(accountFeature.getAllowedOperations().size() > 0)
				logger.debug("Checking allowed operations for AccountFeature {}-{}.",accountFeature.getId(),accountFeature.getLabel());
			else
				logger.debug("No denied allowed for AccountFeature {}-{}.",accountFeature.getId(),accountFeature.getLabel());
		}

        for (ServiceOperation operation : allowedOperations)
		{
			OperationMatch operationMatch = operationMatcher.matchOperation(operation, resource, action);
			if(!(operationMatch == OperationMatch.none))
			{
				match.setAllowMatch(operationMatch);
				match.addAllowedProperties(operation.getProperties());
			}
		}
	}

    protected List<ServiceOperationsHolder<AccountFeature>> getMatchingFeatures(String service, String resource,
                                                                                String action)
    {
        List<ServiceOperationsHolder<AccountFeature>> list = new ArrayList<>();

        List<ServiceOperationsHolder<AccountFeature>> features = accountFeatureCache.loadAll();
        for (ServiceOperationsHolder<AccountFeature> feature : features)
        {
            logger.debug("Checking feature {} for matching operations",feature.getWrapped().getLabel());
            if(checkFeatureForMatch(service,resource,action,feature))
            {
                logger.debug("Found match");
                list.add(feature);
            }
            else
                logger.debug("No match");
        }

        return list;
    }

    protected boolean checkFeatureForMatch(String service, String resource,
                                           String action,ServiceOperationsHolder<AccountFeature> feature)
    {
        for (ServiceOperation operation : feature.getAllowedOperations(service))
        {
            if(operationMatcher.matchOperation(operation,resource,action) !=  OperationMatch.none)
            {
                return true;
            }
        }

        for (ServiceOperation operation : feature.getDeniedOperations(service))
        {
            if(operationMatcher.matchOperation(operation,resource,action) !=  OperationMatch.none)
            {
                return true;
            }
        }

        return false;
    }

	private boolean isSet(Collection collection)
	{
		return collection != null && !collection.isEmpty();
	}
}
