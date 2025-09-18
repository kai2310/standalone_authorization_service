package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "type"})
public class AccountReference extends Reference
{
    private AccountReferenceTypeEnum type;

    public AccountReferenceTypeEnum getType()
    {
        return type;
    }

    public void setType(AccountReferenceTypeEnum type)
    {
        this.type = type;
    }
}
