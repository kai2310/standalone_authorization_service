package com.rubicon.platform.authorization.translator;

public interface ObjectValueConverter<C,P>
{
    public C convertToClientValue(P persistentValue, TranslationContext context);

    public P convertToPersistentValue(C clientValue, TranslationContext context);
}
