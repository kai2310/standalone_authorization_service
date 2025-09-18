package com.rubicon.platform.authorization.service.resolver;

/**
 */
public class AssignmentResolver
{
    protected OperationMatchResult resolveAssignments(AssignedOperationMatches account,
                                                      AssignedOperationMatches accountGroup)
    {
        OperationMatchResult result = new OperationMatchResult();
        for (OperationMatchResult accountGroupMatch : accountGroup.getDetails())
        {
            boolean override = false;
            for (OperationMatchResult accountMatch : account.getDetails())
            {
                // if account overrides account group then use that
                if(accountMatch.overrides(accountGroupMatch))
                    override = true;

                result.add(accountMatch);
            }
            // if this was not overridden by an account match then we add it to the result
            if(!override)
                result.add(accountGroupMatch);
        }

        return result;
    }
}
