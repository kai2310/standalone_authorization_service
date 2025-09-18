package com.rubicon.platform.authorization.service.v1.ui.client.idm;

import com.dottydingo.hyperion.api.ApiObject;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"page","entries"})
public class EntityResponse<T extends ApiObject>
{
    private Page page;
    private List<T> entries;

    public Page getPage()
    {
        return page;
    }

    public void setPage(Page page)
    {
        this.page = page;
    }

    public List<T> getEntries()
    {
        return entries;
    }

    public void setEntries(List<T> entries)
    {
        this.entries = entries;
    }
}
