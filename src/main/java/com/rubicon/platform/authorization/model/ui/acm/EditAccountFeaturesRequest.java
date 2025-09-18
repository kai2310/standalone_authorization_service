package com.rubicon.platform.authorization.model.ui.acm;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"id", "featureIds", "action"})
public class EditAccountFeaturesRequest
{
    private Long id;
    private List<Long> featureIds;
    private EditActionEnum action;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public List<Long> getFeatureIds()
    {
        return featureIds;
    }

    public void setFeatureIds(List<Long> featureIds)
    {
        this.featureIds = featureIds;
    }

    public EditActionEnum getAction()
    {
        return action;
    }

    public void setAction(EditActionEnum action)
    {
        this.action = action;
    }

    public EditAccountFeaturesRequest()
    {
    }

    public EditAccountFeaturesRequest(Long id, List<Long> featureIds, EditActionEnum action)
    {
        this.id = id;
        this.featureIds = featureIds;
        this.action = action;
    }
}
