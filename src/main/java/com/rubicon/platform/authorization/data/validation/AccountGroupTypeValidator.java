package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.validation.ValidationErrorContext;
import com.rubicon.platform.authorization.data.model.PersistentAccountGroupType;
import com.rubicon.platform.authorization.data.persistence.AccountGroupTypeLoader;
import com.rubicon.platform.authorization.model.data.acm.AccountGroupType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 */
public class AccountGroupTypeValidator extends BaseFrameworkValidator<AccountGroupType,PersistentAccountGroupType>
{
    @Autowired
    private AccountGroupTypeLoader accountGroupTypeLoader;

    public void setAccountGroupTypeLoader(AccountGroupTypeLoader accountGroupTypeLoader)
    {
        this.accountGroupTypeLoader = accountGroupTypeLoader;
    }

    @Override
    protected void validateCreate(AccountGroupType clientObject, ValidationErrorContext errorContext,
                                  PersistenceContext persistenceContext)
    {
        validateRequired(errorContext,"label",clientObject.getLabel());
        validateNotBlank(errorContext,"label",clientObject.getLabel());
        validateLength(errorContext,"label",clientObject.getLabel(),64);
        validateReadOnly(errorContext,"status",clientObject.getStatus());

    }

    @Override
    protected void validateUpdate(AccountGroupType clientObject, PersistentAccountGroupType persistentObject,
                                  ValidationErrorContext errorContext, PersistenceContext persistenceContext)
    {
        validateNotBlank(errorContext,"label",clientObject.getLabel());
        validateLength(errorContext,"label",clientObject.getLabel(),64);
        validateNotChanged(errorContext,"status",clientObject.getStatus(),persistentObject.getStatus());

    }

    @Override
    protected void validateCreateConflict(AccountGroupType clientObject, ValidationErrorContext errorContext,
                                          PersistenceContext persistenceContext)
    {
        if(StringUtils.isNotBlank(clientObject.getLabel()) && !accountGroupTypeLoader.isLabelUnique(clientObject.getLabel()))
            errorContext.addValidationError(NOT_UNIQUE,"label",clientObject.getLabel(),"label");
    }

    @Override
    protected void validateUpdateConflict(AccountGroupType clientObject, PersistentAccountGroupType persistentObject,
                                          ValidationErrorContext errorContext, PersistenceContext persistenceContext)
    {
        if(valueChanged(clientObject.getLabel(),persistentObject.getLabel())
           && StringUtils.isNotBlank(clientObject.getLabel())
           && !accountGroupTypeLoader.isLabelUnique(clientObject.getLabel(),persistentObject.getId()))
            errorContext.addValidationError(NOT_UNIQUE,"label",clientObject.getLabel(),"label");
    }

    @Override
    protected void validateDeleteConflict(PersistentAccountGroupType persistentObject, ValidationErrorContext errorContext,
                                          PersistenceContext persistenceContext)
    {
        if(accountGroupTypeLoader.hasReferences(persistentObject.getId()))
            errorContext.addValidationError(DELETE_CONFLICT,"id","AccountGroupType",persistentObject.getId().toString());
    }

}
