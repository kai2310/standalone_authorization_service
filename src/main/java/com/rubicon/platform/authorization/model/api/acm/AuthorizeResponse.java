package com.rubicon.platform.authorization.model.api.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

@JsonPropertyOrder({"service","resource","action","authorized", "reason","authorizedAccounts","scope"})
public class AuthorizeResponse implements Serializable
{
    private String service;
    private String resource;
    private String action;
    private String reason;
    private boolean authorized;
    private List<AuthorizedAccount> authorizedAccounts;
    private List<String> scope;

    public String getService()
    {
        return service;
    }

    public void setService(String service)
    {
        this.service = service;
    }

    public String getResource()
    {
        return resource;
    }

    public void setResource(String resource)
    {
        this.resource = resource;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public boolean getAuthorized()
    {
        return authorized;
    }

    public void setAuthorized(boolean authorized)
    {
        this.authorized = authorized;
    }

    public List<AuthorizedAccount> getAuthorizedAccounts()
    {
        return authorizedAccounts;
    }

    public void setAuthorizedAccounts(List<AuthorizedAccount> authorizedAccounts)
    {
        this.authorizedAccounts = authorizedAccounts;
    }

    public List<String> getScope()
    {
        return scope;
    }

    public void setScope(List<String> scope)
    {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorizeResponse that = (AuthorizeResponse) o;

        if (authorized != that.authorized) return false;
        if (service != null
            ? !service.equals(that.service)
            : that.service != null)
        {
            return false;
        }
        if (resource != null
            ? !resource.equals(that.resource)
            : that.resource != null)
        {
            return false;
        }
        if (action != null
            ? !action.equals(that.action)
            : that.action != null)
        {
            return false;
        }
        if (reason != null
            ? !reason.equals(that.reason)
            : that.reason != null)
        {
            return false;
        }
        if (authorizedAccounts != null
            ? !authorizedAccounts.equals(that.authorizedAccounts)
            : that.authorizedAccounts != null)
        {
            return false;
        }
        return scope != null
               ? scope.equals(that.scope)
               : that.scope == null;

    }

    @Override
    public int hashCode()
    {
        int result = service != null
                     ? service.hashCode()
                     : 0;
        result = 31 * result + (resource != null
                                ? resource.hashCode()
                                : 0);
        result = 31 * result + (action != null
                                ? action.hashCode()
                                : 0);
        result = 31 * result + (reason != null
                                ? reason.hashCode()
                                : 0);
        result = 31 * result + (authorized
                                ? 1
                                : 0);
        result = 31 * result + (authorizedAccounts != null
                                ? authorizedAccounts.hashCode()
                                : 0);
        result = 31 * result + (scope != null
                                ? scope.hashCode()
                                : 0);
        return result;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AuthorizeResponse{");
        sb.append("service='").append(service).append('\'');
        sb.append(", resource='").append(resource).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append(", reason='").append(reason).append('\'');
        sb.append(", authorized=").append(authorized);
        sb.append(", authorizedAccounts=").append(authorizedAccounts);
        sb.append(", scope=").append(scope);
        sb.append('}');
        return sb.toString();
    }
}