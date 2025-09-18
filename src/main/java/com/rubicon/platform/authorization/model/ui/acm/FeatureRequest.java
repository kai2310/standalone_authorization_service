package com.rubicon.platform.authorization.model.ui.acm;

public class FeatureRequest
{
    private Long id;
    private String name;

    public FeatureRequest()
    {
    }

    public FeatureRequest(String name)
    {
        this.name = name;
    }

    public FeatureRequest(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
