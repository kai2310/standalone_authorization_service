package com.rubicon.platform.authorization.translator;

public interface ObjectFieldMapper <C,P>
{
    String getClientFieldName();

    boolean convertToClient(ObjectWrapper<P> persistentObjectWrapper, ObjectWrapper<C> clientObjectWrapper,
                            TranslationContext context);

    boolean convertToPersistent(ObjectWrapper<C> clientObjectWrapper, ObjectWrapper<P> persistentObjectWrapper,
                                TranslationContext context);
}
