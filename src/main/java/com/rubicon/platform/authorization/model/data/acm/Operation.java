package com.rubicon.platform.authorization.model.data.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

/**
 */
@JsonPropertyOrder({"service","resource","action","properties"})
public class Operation implements Serializable
{
    private String service;

    private String resource;

    private String action;

    private List<String> properties;

    public Operation()
    {
    }

    public Operation(String service, String resource, String action)
    {
        this.service = service;
        this.resource = resource;
        this.action = action;
    }

    public Operation(String service, String resource, String action, List<String> properties)
    {
        this.service = service;
        this.resource = resource;
        this.action = action;
        this.properties = properties;
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

    public List<String> getProperties()
    {
        return properties;
    }

    public void setProperties(List<String> properties)
    {
        this.properties = properties;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("Operation");
        sb.append("{service='").append(service).append('\'');
        sb.append(", resource='").append(resource).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append(", properties=").append(properties);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        if (action != null
            ? !action.equals(operation.action)
            : operation.action != null) return false;
        if (properties != null
            ? !properties.equals(operation.properties)
            : operation.properties != null) return false;
        if (resource != null
            ? !resource.equals(operation.resource)
            : operation.resource != null) return false;
        if (service != null
            ? !service.equals(operation.service)
            : operation.service != null) return false;

        return true;
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
        result = 31 * result + (properties != null
                                ? properties.hashCode()
                                : 0);
        return result;
    }
}
