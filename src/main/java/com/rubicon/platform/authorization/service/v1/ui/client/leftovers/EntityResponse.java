package com.rubicon.platform.authorization.service.v1.ui.client.leftovers;

import com.dottydingo.hyperion.api.ApiObject;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"page","content"})
public class EntityResponse<T extends ApiObject>
{
    private Page page;
    private List<T> content;

    public Page getPage()
    {
        return page;
    }

    public void setPage(Page page)
    {
        this.page = page;
    }

    public List<T> getContent()
    {
        return content;
    }

    public void setContent(List<T> content)
    {
        this.content = content;
    }
}
