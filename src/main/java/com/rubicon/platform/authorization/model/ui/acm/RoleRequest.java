package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "type"})
public class RoleRequest
{
    private Long id;
    private String name;
    private RoleTypeEnum type;

    public RoleRequest()
    {
    }

    public RoleRequest(String name, RoleTypeEnum type)
    {
        this.name = name;
        this.type = type;
    }

    public RoleRequest(Long id, String name, RoleTypeEnum type)
    {
        this.id = id;
        this.name = name;
        this.type = type;
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

    public RoleTypeEnum getType()
    {
        return type;
    }

    public void setType(RoleTypeEnum type)
    {
        this.type = type;
    }
}
