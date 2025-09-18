package com.rubicon.platform.authorization.model.api.pmg;

import java.util.List;

public class PublisherIdsRequest
{
    private List<Long> publisherIds;

    public PublisherIdsRequest()
    {
    }

    public List<Long> getPublisherIds()
    {
        return this.publisherIds;
    }

    public void setPublisherIds(List<Long> publisherIds)
    {
        this.publisherIds = publisherIds;
    }
}
