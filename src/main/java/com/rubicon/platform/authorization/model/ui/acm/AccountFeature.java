package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"id", "name", "contextType", "contextId", "status", "allowedAction", "features"})
public class AccountFeature
{
    private Long id;
    private String name;
    private String contextType;
    private Long contextId;
    private String status;
    private AccountFeatureActionEnum allowedAction;
    private List<Reference> features;

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

    public String getContextType()
    {
        return contextType;
    }

    public void setContextType(String contextType)
    {
        this.contextType = contextType;
    }

    public Long getContextId()
    {
        return contextId;
    }

    public void setContextId(Long contextId)
    {
        this.contextId = contextId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public AccountFeatureActionEnum getAllowedAction()
    {
        return allowedAction;
    }

    public void setAllowedAction(AccountFeatureActionEnum allowedAction)
    {
        this.allowedAction = allowedAction;
    }

    public List<Reference> getFeatures()
    {
        return features;
    }

    public void setFeatures(List<Reference> features)
    {
        this.features = features;
    }
}
