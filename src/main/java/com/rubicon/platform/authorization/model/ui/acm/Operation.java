package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"service", "resource", "action", "properties"})
public class Operation
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
}
