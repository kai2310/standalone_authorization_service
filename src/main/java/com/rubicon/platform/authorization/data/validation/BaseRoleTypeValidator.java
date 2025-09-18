package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.exception.ConflictException;
import com.dottydingo.hyperion.api.exception.ValidationException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.data.model.BaseLabeledPersistentObject;
import com.rubicon.platform.authorization.data.model.PersistentOperation;
import com.rubicon.platform.authorization.data.util.JsonUtil;
import com.rubicon.platform.authorization.model.data.acm.BaseRoleApiObject;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * User: mhellkamp
 * Date: 11/28/12
 */
public abstract class BaseRoleTypeValidator<C extends BaseRoleApiObject,P extends BaseLabeledPersistentObject>
		extends BaseAccountValidator<C,P>
{

	public static final String WILDCARD = "*";

	protected void validateOperation(String fieldName, Operation operation)
	{
        assertRequired(fieldName,operation);
		assertRequired(fieldName + ".service", operation.getService());
		if(operation.getService().equals(WILDCARD))
			throw new ValidationException("Service can not be a wildcard.");

        validateLength(fieldName + ".service", operation.getService(), 256);
        assertLatin(fieldName + ".service", operation.getService());
        assertRequired(fieldName + ".resource", operation.getResource());
        validateLength(fieldName + ".resource", operation.getResource(), 256);
        assertLatin(fieldName + ".resource", operation.getResource());
		assertRequired(fieldName + ".action", operation.getAction());
        validateLength(fieldName + ".action", operation.getAction(), 256);
        assertLatin(fieldName + ".action", operation.getAction());

		if(operation.getResource().equals(WILDCARD) && !operation.getAction().equals(WILDCARD))
			throw new ValidationException(
					String.format("%s.action must be a wildcard if %s.resource is a wildcard.",fieldName,fieldName));

		assertNoDuplicates(fieldName + ".properties",operation.getProperties());
        assertLatin(fieldName + ".properties",operation.getProperties());
	}

	protected void validateOperations(String fieldName,List<Operation> operations)
	{
		if(operations == null) return;
		for (Operation operation : operations)
		{
			validateOperation(fieldName,operation);
		}

		checkForDuplicateOperationsInInput(fieldName, operations);
	}

	@Override
	public void validateCreate(C clientObject,PersistenceContext context)
	{
		assertRequired("label",clientObject.getLabel());
        validateLength("label",clientObject.getLabel(),64);
        validateLength("realm",clientObject.getRealm(),64);
		validateOperations("allowedOperations",clientObject.getAllowedOperations());
		validateOperations("deniedOperations",clientObject.getDeniedOperations());

		if(clientObject.getAllowedOperations() != null && clientObject.getDeniedOperations() != null)
		{
			checkForDuplicateOperations(clientObject.getAllowedOperations(),clientObject.getDeniedOperations());
		}
		if(StringUtils.isNotBlank(clientObject.getLabel()) && !isUnique(clientObject.getLabel()))
			throw new ConflictException(
					String.format("The value \"%s\" is not unique for the label field.",clientObject.getLabel()));
	}

	@Override
	public void validateUpdate(C clientObject, P persistentObject,PersistenceContext context)
	{
		assertNotBlank("label", clientObject.getLabel());
        validateLength("label",clientObject.getLabel(),64);
        validateLength("realm",clientObject.getLabel(),64);
		validateOperations("allowedOperations",clientObject.getAllowedOperations());
		validateOperations("deniedOperations",clientObject.getDeniedOperations());

		boolean allowedSet = isSet(clientObject.getAllowedOperations());
		boolean deniedSet = isSet(clientObject.getDeniedOperations());
		if(allowedSet && deniedSet)
		{
			checkForDuplicateOperations(clientObject.getAllowedOperations(),clientObject.getDeniedOperations());
		}

		if(valueChanged(clientObject.getLabel(),persistentObject.getLabel())
		   && StringUtils.isNotBlank(clientObject.getLabel())
		   && !isUnique(clientObject.getLabel(),persistentObject.getId()))
			throw new ConflictException(
					String.format("The value \"%s\" is not unique for the label field.",clientObject.getLabel()));
	}


	protected void checkForDuplicateOperationsInInput(String fieldName, List<Operation> operations)
	{
		Set<Operation> operationsSet = new HashSet<Operation>();
		for (Operation operation : operations)
		{
			if(!operationsSet.add(operation))
				throw new ValidationException(String.format("Duplicate operation detected in %s: %s",fieldName,operation));
		}
	}

	protected void checkForDuplicateOperations(List<Operation> allowedOperations,List<Operation> deniedOperations)
	{
		Set<Operation> operationsSet = new HashSet<Operation>(allowedOperations);
		for (Operation operation : deniedOperations)
		{
			if(!operationsSet.add(operation))
				throw new ValidationException(
						String.format("Duplicate operation detected in both allowed and denied operations: %s",operation));
		}
	}

	protected boolean isSet(Collection collection)
	{
		return collection != null && !collection.isEmpty();
	}

	protected List<Operation> convertToOperations(List<PersistentOperation> operations, boolean denied)
	{
		List<Operation> ops = new ArrayList<Operation>();
		for (PersistentOperation persistentOperation : operations)
		{
			if(persistentOperation.isDeny() == denied)
			{
				Operation operation = new Operation();
				operation.setAction(persistentOperation.getAction());
				operation.setResource(persistentOperation.getResource());
				operation.setService(persistentOperation.getService());
				operation.setProperties(JsonUtil.toList(persistentOperation.getProperties()));
				ops.add(operation);
			}
		}

		return ops;
	}

	protected abstract boolean isUnique(String label);
	protected abstract boolean isUnique(String label, Long id);
}
