package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.ErrorDetail;
import com.dottydingo.hyperion.api.exception.ConflictException;
import com.dottydingo.hyperion.api.exception.HyperionException;
import com.dottydingo.hyperion.api.exception.ValidationException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.rubicon.platform.authorization.data.model.PersistentAccountFeature;
import com.rubicon.platform.authorization.data.persistence.AccountFeatureLoader;
import com.rubicon.platform.authorization.model.data.acm.AccountFeature;
import com.rubicon.platform.authorization.model.data.acm.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * User: mhellkamp
 * Date: 10/16/12
 */
public class AccountFeatureValidator extends BaseRoleTypeValidator<AccountFeature,PersistentAccountFeature>
{
	@Autowired
	private AccountFeatureLoader accountFeatureLoader;

	protected void setAccountFeatureLoader(AccountFeatureLoader accountFeatureLoader)
	{
		this.accountFeatureLoader = accountFeatureLoader;
	}

	@Override
	public void validateCreate(AccountFeature clientObject, PersistenceContext context)
	{
		try
		{
			super.validateCreate(clientObject, context);
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
	public void validateUpdate(AccountFeature clientObject, PersistentAccountFeature persistentObject,PersistenceContext context)
	{
		try
		{
			super.validateUpdate(clientObject, persistentObject, context);
			assertNotChanged("realm", clientObject.getRealm(), persistentObject.getRealm());

			boolean allowedSet = isSet(clientObject.getAllowedOperations());
			boolean deniedSet = isSet(clientObject.getDeniedOperations());

			if ((allowedSet && deniedSet) || (!allowedSet && !deniedSet))
				return;

			List<Operation> allowed = null;
			List<Operation> denied = null;
			if (allowedSet)
			{
				allowed = clientObject.getAllowedOperations();
				denied = convertToOperations(persistentObject.getOperations(), true);
			}
			else
			{
				allowed = convertToOperations(persistentObject.getOperations(), false);
				denied = clientObject.getDeniedOperations();
			}

			checkForDuplicateOperations(allowed, denied);
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
	public void validateDelete(PersistentAccountFeature persistentObject,PersistenceContext context)
	{
		try
		{
			if (accountFeatureLoader.hasReferences(persistentObject.getId()))
				throw new ConflictException(
						String.format("AccountFeature id %d is being referenced and can not be deleted.",
								persistentObject.getId()));
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
	protected boolean isUnique(String label)
	{
		return accountFeatureLoader.isLabelUnique(label);
	}

	@Override
	protected boolean isUnique(String label, Long id)
	{
		return accountFeatureLoader.isLabelUnique(label,id);
	}
}
