package com.rubicon.platform.authorization.test.testmodel;

import com.rubicon.platform.authorization.translator.DefaultObjectTranslator;

import javax.annotation.PostConstruct;

public class FakeTranslator extends DefaultObjectTranslator<FakeAPIModel, FakeDataModel>
{
    public FakeTranslator()
    {
        super(FakeAPIModel.class, FakeDataModel.class);
    }

    @Override
    @PostConstruct
    public void init()
    {
        super.init();
    }
}
