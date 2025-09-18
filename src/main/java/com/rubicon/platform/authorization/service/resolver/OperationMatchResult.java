package com.rubicon.platform.authorization.service.resolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 */
public class OperationMatchResult
{

    private OperationMatch denyMatch = OperationMatch.none;
    private OperationMatch allowMatch = OperationMatch.none;

    private Set<String> allowedProperties = new HashSet<String>();
    private Set<String> deniedProperties = new HashSet<String>();

    private Set<String> scope = new HashSet<>();

    public OperationMatchResult()
    {
    }

    public OperationMatchResult(OperationMatchResult other)
    {
        this.denyMatch = other.denyMatch;
        this.allowMatch = other.allowMatch;
        this.allowedProperties = other.allowedProperties;
        this.deniedProperties = other.deniedProperties;
        this.scope = other.scope;
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

    public Set<String> getAllowedProperties()
    {
        return allowedProperties;
    }

    public Set<String> getDeniedProperties()
    {
        return deniedProperties;
    }

    public OperationMatch getDenyMatch()
    {
        return denyMatch;
    }

    public OperationMatch getAllowMatch()
    {
        return allowMatch;
    }

    public boolean isAuthorized()
    {
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

    public void addScope(Collection<String> scope)
    {
        if(scope != null)
            this.scope.addAll(scope);
    }

    public Set<String> getScope()
    {
        return scope;
    }

    public boolean overrides(OperationMatchResult other)
    {
        return (this.allowMatch.canOverride(other.allowMatch) && this.allowMatch.canOverride(other.denyMatch))
                || (this.denyMatch.canOverride(other.allowMatch) && this.denyMatch.canOverride(other.denyMatch));
    }

    public OperationMatchResult merge(OperationMatchResult other)
    {
        OperationMatchResult result = new OperationMatchResult(other);
        result.addAllowedProperties(this.allowedProperties);
        result.addDeniedProperties(this.deniedProperties);
        result.setDenyMatch(this.denyMatch);
        result.setAllowMatch(this.allowMatch);
        result.addScope(this.scope);

        return result;
    }

    public void add(OperationMatchResult other)
    {
        addAllowedProperties(other.allowedProperties);
        addDeniedProperties(other.deniedProperties);
        setDenyMatch(other.denyMatch);
        setAllowMatch(other.allowMatch);
        addScope(other.scope);
    }
}
