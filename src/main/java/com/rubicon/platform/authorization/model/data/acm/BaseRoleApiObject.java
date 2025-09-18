package com.rubicon.platform.authorization.model.data.acm;

import com.dottydingo.hyperion.api.BaseAuditableApiObject;

import java.io.Serializable;
import java.util.List;

public abstract class BaseRoleApiObject extends BaseAuditableApiObject<Long> implements Serializable
{
    private String label;
    private String realm;
    private String description;
    private List<Operation> allowedOperations;
    private List<Operation> deniedOperations;

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getRealm()
    {
        return realm;
    }

    public void setRealm(String realm)
    {
        this.realm = realm;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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
}
