package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.validation.ValidationErrorContext;

/**
 */
public class BaseHyperionValidatorFixture
{
    protected PersistenceContext persistenceContext;
    protected ValidationErrorContext errorContext;

    public void setUp() throws Exception
    {
        persistenceContext = new PersistenceContext();
        errorContext = new ValidationErrorContext();
    }
}
