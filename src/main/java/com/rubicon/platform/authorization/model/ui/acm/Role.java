package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"id", "name", "type", "allowedOperations", "deniedOperations", "editable"})
public class Role
{
    private Long id;
    private String name;
    private RoleTypeEnum type;
    private List<Operation> allowedOperations;
    private List<Operation> deniedOperations;
    private Boolean editable;

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

    public List<Operation> getAllowedOperations()
    {
        return allowedOperations;
    }

    public void setAllowedOperations(List<Operation> allowedOperations)
    {
        this.allowedOperations = allowedOperations;
    }

    public List<Operation> getDeniedOperations()
    {
        return deniedOperations;
    }

    public void setDeniedOperations(List<Operation> deniedOperations)
    {
        this.deniedOperations = deniedOperations;
    }

    public void setType(RoleTypeEnum type)
    {
        this.type = type;
    }

    public Boolean getEditable()
    {
        return editable;
    }

    public void setEditable(Boolean editable)
    {
        this.editable = editable;
    }
}
