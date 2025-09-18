package com.rubicon.platform.authorization.translator;

public interface ObjectTranslator<C,P>
{
    P convertClient(C client, TranslationContext translationContext);

    boolean copyClient(C client, P persistent, TranslationContext translationContext);

    C convertPersistent(P persistent, TranslationContext translationContext);

    void copyPersistent(P persistent, C client, TranslationContext translationContext);
}
