package com.rubicon.platform.authorization.test.testmodel;

import com.rubicon.platform.authorization.service.v1.ui.resolver.BaseServiceResolver;

public class FakeServiceResolver extends BaseServiceResolver<FakeAPIModel, FakeDataModel>
{
    public FakeServiceResolver()
    {
        FakeTranslator translator = new FakeTranslator();
        translator.init();
        setTranslator(translator);
    }
}
