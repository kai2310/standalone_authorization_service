package com.rubicon.platform.authorization.model.api.acm;

import java.io.Serializable;

public class BaseAuthorizeRequest implements Serializable
{
    private String accountContext;
    private String service;
    private String resource;
    private String action;

    public String getAccountContext()
    {
        return accountContext;
    }

    public void setAccountContext(String accountContext)
    {
        this.accountContext = accountContext;
    }

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

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("AuthorizeRequest{");
        sb.append(", accountContext='").append(accountContext).append('\'');
        sb.append(", service='").append(service).append('\'');
        sb.append(", resource='").append(resource).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof com.rubicon.platform.authorization.model.api.acm.BaseAuthorizeRequest)) return false;

        BaseAuthorizeRequest that = (BaseAuthorizeRequest) o;

        if (accountContext != null
            ? !accountContext.equals(that.accountContext)
            : that.accountContext != null)
        {
            return false;
        }
        if (!service.equals(that.service)) return false;
        if (!resource.equals(that.resource)) return false;
        return action.equals(that.action);

    }

    @Override
    public int hashCode()
    {
        int result = accountContext != null
                     ? accountContext.hashCode()
                     : 0;
        result = 31 * result + service.hashCode();
        result = 31 * result + resource.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }
}