package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "role", "accountReference", "editable"})
public class RoleAssignment
{
    private Long id;
    private Reference role;
    private AccountReference accountReference;
    private Boolean editable;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Reference getRole()
    {
        return role;
    }

    public void setRole(Reference role)
    {
        this.role = role;
    }

    public AccountReference getAccountReference()
    {
        return accountReference;
    }

    public void setAccountReference(AccountReference accountReference)
    {
        this.accountReference = accountReference;
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
