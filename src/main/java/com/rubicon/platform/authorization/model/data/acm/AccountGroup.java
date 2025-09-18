package com.rubicon.platform.authorization.model.data.acm;

import com.dottydingo.hyperion.api.BaseAuditableApiObject;
import com.dottydingo.hyperion.api.Endpoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rubicon.platform.authorization.model.data.acm.Status;

import java.io.Serializable;
import java.util.List;

@Endpoint("AccountGroup")
@JsonPropertyOrder({"id","label","description","accountGroupTypeId","accountIds","accountType","status",
        "created","createdBy","modified","modifiedBy"})
public class AccountGroup extends BaseAuditableApiObject<Long> implements Serializable
{
    private String label;
    private String description;
    private Long accountGroupTypeId;
    private List<Long> accountIds;
    private String accountType;
    private Status status;

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getAccountGroupTypeId()
    {
        return accountGroupTypeId;
    }

    public void setAccountGroupTypeId(Long accountGroupTypeId)
    {
        this.accountGroupTypeId = accountGroupTypeId;
    }

    public List<Long> getAccountIds()
    {
        return accountIds;
    }

    public void setAccountIds(List<Long> accountIds)
    {
        this.accountIds = accountIds;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }
}
