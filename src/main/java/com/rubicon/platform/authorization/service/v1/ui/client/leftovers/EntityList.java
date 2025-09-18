package com.rubicon.platform.authorization.service.v1.ui.client.leftovers;

import java.util.List;

public class EntityList<T>
{
    private List<T> content;

    public EntityList()
    {

    }

    public EntityList(List<T> content)
    {
        this.content = content;
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
