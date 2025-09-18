package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 */
@Component
public class SubjectAccountMatchResolver
{
    private static Logger logger = LoggerFactory.getLogger(SubjectAccountMatchResolver.class);
    private AssignmentResolver assignmentResolver = new AssignmentResolver();

    @Autowired
    private AccountResolver accountResolver;

    protected void setAccountResolver(AccountResolver accountResolver)
    {
        this.accountResolver = accountResolver;
    }

    public SubjectAccountMatches resolve(CompoundId requestedAccountContext,
                                         ResolvedSubjectAccounts resolvedSubjectAccounts)
    {
        AccountMatchType accountMatchType = resolvedSubjectAccounts.getAccountMatchType();

        if(requestedAccountContext != null)
        {
            // account is explicitly requested so our scope is already constrained

            switch (accountMatchType)
            {
                case ACCOUNT:
                    return buildForSingleAccount(requestedAccountContext, resolvedSubjectAccounts.getAccountAggregate());
                case ACCOUNT_GROUP:
                    return buildForSingleAccount(requestedAccountContext,
                            resolvedSubjectAccounts.getAccountGroupAggregate());
                case MIXED:
                {

                    AssignedOperationMatches accountMatch = resolvedSubjectAccounts.getAccountMatches().get(
                            requestedAccountContext);

                    OperationMatchResult filteredMatches = new OperationMatchResult();

                    for (Map.Entry<Long, AssignedOperationMatches> entry : resolvedSubjectAccounts.getAccountGroupMatches().entrySet())
                    {
                        if (entry.getValue().getAggregate().isAuthorized())
                            logger.debug("AccountGroup ID: {} is authorized.", entry.getKey());
                        else
                            logger.debug("AccountGroup ID: {} is not authorized.", entry.getKey());


                        OperationMatchResult resolvedAccountResult = assignmentResolver.resolveAssignments(accountMatch, entry.getValue());;
                        filteredMatches.add(resolvedAccountResult);
                    }


                    return buildForSingleAccount(requestedAccountContext, filteredMatches);
                }
                default:
                    logger.debug("Subject is not authorized for Account {}.",requestedAccountContext);
                    return new SubjectAccountMatches();
            }
        }
        else
        {
            // the account was not explicitly requested so we need to return all matches.
            switch (accountMatchType)
            {
                case ACCOUNT:
                {
                    return buildForOperationMatches(resolvedSubjectAccounts.getAccountMatches());
                }
                case ACCOUNT_GROUP:
                {
                    // resolve the Account Ids for the Account Groups
                    Map<CompoundId,AssignedOperationMatches> matches = new HashMap<>();
                    for (Map.Entry<Long, AssignedOperationMatches> entry : resolvedSubjectAccounts.getAccountGroupMatches().entrySet())
                    {
                        if(entry.getValue().getAggregate().isAuthorized())
                            logger.debug("AccountGroup ID: {} is authorized.",entry.getKey());
                        else
                            logger.debug("AccountGroup ID: {} is not authorized.",entry.getKey());

                        Collection<CompoundId> ids = accountResolver.resolveAccountIds(entry.getKey());
                        for (CompoundId id : ids)
                        {
                            logger.debug("Resolved Account {}", id);
                            AssignedOperationMatches match = matches.get(id);
                            if (match == null)
                            {
                                match = new AssignedOperationMatches();
                                matches.put(id, match);
                            }
                            match.add(entry.getValue());
                        }
                    }

                   return buildForOperationMatches(matches);
                }
                case MIXED:
                {
                    Map<CompoundId,AssignedOperationMatches> matches = resolvedSubjectAccounts.getAccountMatches();

                    Map<CompoundId,OperationMatchResult> filteredMatches = new HashMap<>();
                    for (Map.Entry<Long, AssignedOperationMatches> entry : resolvedSubjectAccounts.getAccountGroupMatches().entrySet())
                    {
                        if (entry.getValue().getAggregate().isAuthorized())
                            logger.debug("AccountGroup ID: {} is authorized.",entry.getKey());
                        else
                            logger.debug("AccountGroup ID: {} is not authorized.",entry.getKey());

                        Collection<CompoundId> ids = accountResolver.resolveAccountIds(entry.getKey());
                        for (CompoundId id : ids)
                        {
                            logger.debug("Resolved Account {}",id);
                            AssignedOperationMatches match = matches.get(id);

                            OperationMatchResult resolvedAccountResult = null;
                            if(match != null)
                                resolvedAccountResult = assignmentResolver.resolveAssignments(match, entry.getValue());
                            else
                                resolvedAccountResult = entry.getValue().getAggregate();

                            OperationMatchResult accountResult = filteredMatches.get(id);
                            if(accountResult == null)
                            {
                                accountResult = new OperationMatchResult();
                                filteredMatches.put(id, accountResult);
                            }
                            accountResult.add(resolvedAccountResult);
                        }
                    }

                    Map<CompoundId,OperationMatchResult> filtered = new HashMap<>();

                    // merge in account results
                    for (Map.Entry<CompoundId, AssignedOperationMatches> entry : matches.entrySet())
                    {
                        OperationMatchResult resolved = filteredMatches.get(entry.getKey());
                        if(resolved == null)
                            resolved = entry.getValue().getAggregate();

                        if(resolved.isAuthorized())
                        {
                            logger.debug("Account ID: {} is authorized.",entry.getKey());
                            filtered.put(entry.getKey(),resolved);
                        }
                        else
                            logger.debug("Account ID: {} is not authorized.",entry.getKey());

                    }

                    // now add in anything that did not have an account result
                    for (Map.Entry<CompoundId, OperationMatchResult> entry : filteredMatches.entrySet())
                    {
                        if(!filtered.containsKey(entry.getKey()))
                        {
                            if(entry.getValue().isAuthorized())
                            {
                                logger.debug("Account ID: {} is authorized.",entry.getKey());
                                filtered.put(entry.getKey(),entry.getValue());
                            }
                            else
                                logger.debug("Account ID: {} is not authorized.",entry.getKey());
                        }
                    }

                    return buildForAccounts(filtered);
                }
                default:
                    logger.debug("Subject is not authorized.");
                    return new SubjectAccountMatches();
            }
        }

    }


    private SubjectAccountMatches buildForSingleAccount(CompoundId account, OperationMatchResult result)
    {
        if(result.isAuthorized())
        {
            logger.debug("Subject is authorized for Account {}.",account);
            return new SubjectAccountMatches(account,
                    new AccountProperties(result.getAllowedProperties(), result.getDeniedProperties()),
                    result.getScope());
        }

        logger.debug("Subject is not authorized for Account {}.",account);
        return new SubjectAccountMatches();
    }

    private SubjectAccountMatches buildForAccounts(Map<CompoundId, OperationMatchResult> entries)
    {
        Set<String> scope = new HashSet<>();
        Map<CompoundId,AccountProperties> map = new HashMap<>();
        for (Map.Entry<CompoundId, OperationMatchResult> entry : entries.entrySet())
        {
            OperationMatchResult result = entry.getValue();
            if(result.isAuthorized())
            {
                logger.debug("Subject is authorized for Account {}.",entry.getKey());
                map.put(entry.getKey(),
                        new AccountProperties(result.getAllowedProperties(), result.getDeniedProperties()));
                scope.addAll(result.getScope());
            }
            else
            {
                logger.debug("Subject is not authorized for Account {}.",entry.getKey());
            }
        }

        return new SubjectAccountMatches(map,scope);
    }



    // general case where we collapse accounts
    private SubjectAccountMatches buildForOperationMatches(Map<CompoundId, AssignedOperationMatches> entries)
    {
        Set<String> scope = new HashSet<>();
        Map<CompoundId,AccountProperties> map = new HashMap<>();
        for (Map.Entry<CompoundId, AssignedOperationMatches> entry : entries.entrySet())
        {
            OperationMatchResult result = entry.getValue().getAggregate();

            if(result.isAuthorized())
            {
                logger.debug("Subject is authorized for Account {}.",entry.getKey());
                map.put(entry.getKey(),
                        new AccountProperties(result.getAllowedProperties(), result.getDeniedProperties()));
                scope.addAll(result.getScope());
            }
            else
            {
                logger.debug("Subject is not authorized for Account {}.",entry.getKey());
            }
        }

        return new SubjectAccountMatches(map,scope);
    }


}
