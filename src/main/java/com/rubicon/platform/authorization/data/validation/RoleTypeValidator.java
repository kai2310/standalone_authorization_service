package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.validation.DefaultValidator;
import com.dottydingo.hyperion.core.validation.ValidationErrorContext;
import com.rubicon.platform.authorization.data.model.PersistentRoleType;
import com.rubicon.platform.authorization.data.persistence.RoleTypeLoader;
import com.rubicon.platform.authorization.model.data.acm.RoleType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 */
public class RoleTypeValidator extends BaseFrameworkValidator<RoleType,PersistentRoleType>
{
    @Autowired
    private RoleTypeLoader roleTypeLoader;

    protected void setRoleTypeLoader(RoleTypeLoader roleTypeLoader)
    {
        this.roleTypeLoader = roleTypeLoader;
    }

    @Override
    protected void validateCreate(RoleType clientObject, ValidationErrorContext errorContext,
                                  PersistenceContext persistenceContext)
    {
        validateRequired(errorContext,"label",clientObject.getLabel());
        validateNotBlank(errorContext,"label",clientObject.getLabel());
        validateLength(errorContext,"label",clientObject.getLabel(),64);
        validateReadOnly(errorContext,"status",clientObject.getStatus());
    }


    @Override
    protected void validateUpdate(RoleType clientObject, PersistentRoleType persistentObject,
                                  ValidationErrorContext errorContext, PersistenceContext persistenceContext)
    {
        validateNotBlank(errorContext,"label",clientObject.getLabel());
        validateLength(errorContext,"label",clientObject.getLabel(),64);
        validateNotChanged(errorContext,"status",clientObject.getStatus(),persistentObject.getStatus());
    }

    @Override
    protected void validateCreateConflict(RoleType clientObject, ValidationErrorContext errorContext,
                                          PersistenceContext persistenceContext)
    {
        if(StringUtils.isNotBlank(clientObject.getLabel()) && !roleTypeLoader.isLabelUnique(clientObject.getLabel()))
            errorContext.addValidationError(NOT_UNIQUE,"label",clientObject.getLabel(),"label");
    }

    @Override
    protected void validateUpdateConflict(RoleType clientObject, PersistentRoleType persistentObject,
                                          ValidationErrorContext errorContext, PersistenceContext persistenceContext)
    {
        if(valueChanged(clientObject.getLabel(),persistentObject.getLabel())
           && StringUtils.isNotBlank(clientObject.getLabel())
           && !roleTypeLoader.isLabelUnique(clientObject.getLabel(),persistentObject.getId()))
            errorContext.addValidationError(NOT_UNIQUE,"label",clientObject.getLabel(),"label");
    }

    @Override
    protected void validateDeleteConflict(PersistentRoleType persistentObject, ValidationErrorContext errorContext,
                                          PersistenceContext persistenceContext)
    {
        if(roleTypeLoader.hasReferences(persistentObject.getId()))
            errorContext.addValidationError(DELETE_CONFLICT,"id","RoleType",persistentObject.getId().toString());
    }

}
