package com.rubicon.platform.authorization.model.ui.acm;

public class RoleType
{
    private RoleTypeEnum id;
    private String name;

    public RoleType()
    {
    }

    public RoleType(RoleTypeEnum id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public RoleTypeEnum getId()
    {
        return id;
    }

    public void setId(RoleTypeEnum id)
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
