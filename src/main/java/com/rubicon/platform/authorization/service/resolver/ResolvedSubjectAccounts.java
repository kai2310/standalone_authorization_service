package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;

import java.util.*;

/**
 */
public class ResolvedSubjectAccounts
{
    private Map<CompoundId,AssignedOperationMatches> accountMatches = new HashMap<>();
    private OperationMatchResult accountAggregate = new OperationMatchResult();
    private Map<Long,AssignedOperationMatches> accountGroupMatches = new HashMap<>();
    private OperationMatchResult accountGroupAggregate = new OperationMatchResult();

    public void addAccountMatch(CompoundId accountId,OperationMatchResult operationMatchResult)
    {
        AssignedOperationMatches existing = accountMatches.get(accountId);
        if(existing == null)
        {
            existing = new AssignedOperationMatches();
            accountMatches.put(accountId, existing);
        }

        existing.add(operationMatchResult);

        accountAggregate.add(operationMatchResult);
    }

    public void addAccountGroupMatch(long accountGroupId,OperationMatchResult operationMatchResult)
    {
        AssignedOperationMatches existing = accountGroupMatches.get(accountGroupId);
        if(existing == null)
        {
            existing = new AssignedOperationMatches();
            accountGroupMatches.put(accountGroupId, existing);
        }

        existing.add(operationMatchResult);

        accountGroupAggregate.add(operationMatchResult);
    }

    public OperationMatchResult getAccountAggregate()
    {
        return accountAggregate;
    }

    public OperationMatchResult getAccountGroupAggregate()
    {
        return accountGroupAggregate;
    }


    public Map<CompoundId, AssignedOperationMatches> getAccountMatches()
    {
        return accountMatches;
    }

    public Map<Long, AssignedOperationMatches> getAccountGroupMatches()
    {
        return accountGroupMatches;
    }


    public AccountMatchType getAccountMatchType()
    {
        if(accountMatches.size() > 0)
        {
            if(accountGroupMatches.size() == 0)
                return AccountMatchType.ACCOUNT;
            else
                return AccountMatchType.MIXED;
        }
        else if(accountGroupMatches.size() > 0)
            return AccountMatchType.ACCOUNT_GROUP;
        else
            return AccountMatchType.NONE;
    }

}
