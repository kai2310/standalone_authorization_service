package com.rubicon.platform.authorization.model.api.acm;

import java.util.Set;

public class AuthorizedAccount
{
    private String accountId;
    private Set<String> allowedProperties;
    private Set<String> deniedProperties;

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public Set<String> getAllowedProperties()
    {
        return allowedProperties;
    }

    public void setAllowedProperties(Set<String> allowedProperties)
    {
        this.allowedProperties = allowedProperties;
    }

    public Set<String> getDeniedProperties()
    {
        return deniedProperties;
    }

    public void setDeniedProperties(Set<String> deniedProperties)
    {
        this.deniedProperties = deniedProperties;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorizedAccount that = (AuthorizedAccount) o;

        if (accountId != null
            ? !accountId.equals(that.accountId)
            : that.accountId != null)
        {
            return false;
        }
        if (allowedProperties != null
            ? !allowedProperties.equals(that.allowedProperties)
            : that.allowedProperties != null)
        {
            return false;
        }
        return deniedProperties != null
               ? deniedProperties.equals(that.deniedProperties)
               : that.deniedProperties == null;

    }

    @Override
    public int hashCode()
    {
        int result = accountId != null
                     ? accountId.hashCode()
                     : 0;
        result = 31 * result + (allowedProperties != null
                                ? allowedProperties.hashCode()
                                : 0);
        result = 31 * result + (deniedProperties != null
                                ? deniedProperties.hashCode()
                                : 0);
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AuthorizedAccount{");
        sb.append("accountId='").append(accountId).append('\'');
        sb.append(", allowedProperties=").append(allowedProperties);
        sb.append(", deniedProperties=").append(deniedProperties);
        sb.append('}');
        return sb.toString();
    }
}
