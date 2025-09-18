package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.ErrorDetail;
import com.dottydingo.hyperion.api.exception.HyperionException;
import com.dottydingo.hyperion.api.exception.ValidationException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.persistence.RoleTypeLoader;
import com.rubicon.platform.authorization.model.data.acm.Role;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 */
public class RoleValidator extends BaseRoleValidator<Role>
{
    @Autowired
    private RoleTypeLoader roleTypeLoader;


    public void setRoleTypeLoader(RoleTypeLoader roleTypeLoader)
    {
        this.roleTypeLoader = roleTypeLoader;
    }

    @Override
    public void validateCreate(Role clientObject, PersistenceContext context)
    {
        try
        {
            super.validateCreate(clientObject, context);
            assertRequired("roleTypeId", clientObject.getRoleTypeId());
            if (clientObject.getRoleTypeId() != null && !roleTypeLoader.exists(clientObject.getRoleTypeId()))
                throw new ValidationException(
                        String.format("RoleType id %d does not exist.", clientObject.getRoleTypeId()));
        }
        catch (HyperionException e)
        {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setMessage(e.getMessage());
            errorDetail.setCode("ERROR");
            e.setErrorDetails(Collections.singletonList(errorDetail));
            throw e;
        }

    }

    @Override
    public void validateUpdate(Role clientObject, PersistentRole persistentObject, PersistenceContext context)
    {
        try
        {
            super.validateUpdate(clientObject, persistentObject, context);
            if (valueChanged(clientObject.getRoleTypeId(), persistentObject.getRoleTypeId()))
            {
                if (!roleTypeLoader.exists(clientObject.getRoleTypeId()))
                    throw new ValidationException(
                            String.format("RoleType id %d does not exist.", clientObject.getRoleTypeId()));
            }
        }
        catch (HyperionException e)
        {
            ErrorDetail errorDetail = new ErrorDetail();
            errorDetail.setMessage(e.getMessage());
            errorDetail.setCode("ERROR");
            e.setErrorDetails(Collections.singletonList(errorDetail));
            throw e;
        }
    }
}
