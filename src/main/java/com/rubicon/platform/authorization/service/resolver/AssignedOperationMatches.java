package com.rubicon.platform.authorization.service.resolver;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class AssignedOperationMatches
{
    private OperationMatchResult aggregate = new OperationMatchResult();
    private List<OperationMatchResult> details = new ArrayList<>();

    public void add(OperationMatchResult operationMatch)
    {
        aggregate.add(operationMatch);
        details.add(operationMatch);
    }

    public OperationMatchResult getAggregate()
    {
        return aggregate;
    }

    public List<OperationMatchResult> getDetails()
    {
        return details;
    }

    public boolean hasDetails()
    {
        return !details.isEmpty();
    }

    public void add(AssignedOperationMatches other)
    {
        aggregate.add(other.aggregate);
        details.addAll(other.details);
    }
}
