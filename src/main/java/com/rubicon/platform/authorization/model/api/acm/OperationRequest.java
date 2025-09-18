package com.rubicon.platform.authorization.model.api.acm;

import java.util.Objects;

public class OperationRequest
{
    private String service;
    private String resource;
    private String action;

    public OperationRequest()
    {
    }

    public OperationRequest(String service, String resource, String action)
    {
        this.service = service;
        this.resource = resource;
        this.action = action;
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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationRequest that = (OperationRequest) o;
        return Objects.equals(service, that.service) &&
               Objects.equals(resource, that.resource) &&
               Objects.equals(action, that.action);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(service, resource, action);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("OperationRequest{");
        sb.append("service='").append(service).append('\'');
        sb.append(", resource='").append(resource).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
