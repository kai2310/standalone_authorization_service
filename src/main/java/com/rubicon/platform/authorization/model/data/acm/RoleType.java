package com.rubicon.platform.authorization.model.data.acm;

import com.dottydingo.hyperion.api.BaseAuditableApiObject;
import com.dottydingo.hyperion.api.Endpoint;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Endpoint("RoleType")
@JsonPropertyOrder({"id","label","description","status",
        "created","createdBy","modified","modifiedBy"})
public class RoleType extends BaseAuditableApiObject<Long>
{
    private String label;
    private String description;
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

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }
}
