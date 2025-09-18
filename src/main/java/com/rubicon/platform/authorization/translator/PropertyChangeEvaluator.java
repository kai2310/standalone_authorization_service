package com.rubicon.platform.authorization.translator;

public interface PropertyChangeEvaluator<T>
{
    boolean hasChanged(T oldValue,T newValue);
}
