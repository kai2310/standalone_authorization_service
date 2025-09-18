package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.exception.ValidationException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.persistence.RoleLoader;
import com.rubicon.platform.authorization.model.data.acm.BaseRole;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 */
public class BaseRoleValidator<C extends BaseRole> extends BaseRoleTypeValidator<C,PersistentRole>
{
	@Autowired
	private RoleLoader roleLoader;

	protected void setRoleLoader(RoleLoader roleLoader)
	{
		this.roleLoader = roleLoader;
	}

	@Override
	public void validateCreate(C clientObject,PersistenceContext context)
	{
		super.validateCreate(clientObject, context);
        if(containsUnicode(clientObject.getOwnerAccount()))
            throw new ValidationException("The \"ownerAccount\" field may not contain unicode characters.");

        assertRequired("ownerAccount",clientObject.getOwnerAccount());
		assertReadOnly("status",clientObject.getStatus());
		validateAccount(clientObject.getOwnerAccount(), WildcardValidator.fullWildcardAllowed);
	}

	@Override
	public void validateUpdate(C clientObject, PersistentRole persistentObject,PersistenceContext context)
	{
		super.validateUpdate(clientObject,persistentObject,context);
        assertNotChanged("realm", clientObject.getRealm(), persistentObject.getRealm());
        assertNotChanged("status",clientObject.getStatus(),persistentObject.getStatus());
		assertNotBlank("ownerAccount",clientObject.getOwnerAccount());

        if(containsUnicode(clientObject.getOwnerAccount()))
            throw new ValidationException("The \"ownerAccount\" field may not contain unicode characters.");

		validateAccount(clientObject.getOwnerAccount(), WildcardValidator.fullWildcardAllowed);

		boolean allowedSet = isSet(clientObject.getAllowedOperations());
		boolean deniedSet = isSet(clientObject.getDeniedOperations());

		if((allowedSet && deniedSet) || (!allowedSet && !deniedSet))
			return;

		List<Operation> allowed = null;
		List<Operation> denied = null;
		if(allowedSet)
		{
			allowed = clientObject.getAllowedOperations();
			denied = convertToOperations(persistentObject.getOperations(),true);
		}
		else
		{
			allowed = convertToOperations(persistentObject.getOperations(),false);
			denied = clientObject.getDeniedOperations();
		}

		checkForDuplicateOperations(allowed,denied);
	}


	@Override
	public void validateDelete(PersistentRole persistentObject,PersistenceContext context)
	{
		if(roleLoader.hasReferences(persistentObject.getId()))
			throw new ValidationException(
					String.format("Role id %d is being referenced and can not be deleted.",persistentObject.getId()));
	}

	@Override
	protected boolean isUnique(String label)
	{
		return roleLoader.isLabelUnique(label);
	}

	@Override
	protected boolean isUnique(String label, Long id)
	{
		return roleLoader.isLabelUnique(label,id);
	}
}
