package com.rubicon.platform.authorization.test.testmodel;

import com.dottydingo.hyperion.api.BaseApiObject;

public class FakeDataModel extends BaseApiObject<Long>
{
    private Long id;
    private String name;

    public FakeDataModel()
    {
    }

    public FakeDataModel(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
