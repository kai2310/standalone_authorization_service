package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.model.PersistentObject;
import com.dottydingo.hyperion.core.validation.Validator;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: mhellkamp
 * Date: 10/16/12
 */
public abstract class BaseValidatorFixture
{

	protected void assertFailsCreate(Class exceptionType,String message, Validator val, ApiObject apiObject)
	{
		try
		{
			val.validateCreate(apiObject,null);
			Assert.fail(String.format("Should have thrown %s", exceptionType.getName()));
		}
		catch (Throwable e)
		{
			if(!exceptionType.isAssignableFrom(e.getClass()))
				Assert.fail(String.format("Should have thrown %s but threw %s",exceptionType.getName(),e.getClass().getName()));

			Assert.assertEquals(message,e.getMessage());
		}
	}

	protected void assertFailsUpdate(Class exceptionType,String message, Validator val, ApiObject apiObject)
	{
		assertFailsUpdate(exceptionType,message,val,apiObject,null);
	}

	protected void assertFailsUpdate(Class exceptionType,String message, Validator val, ApiObject apiObject,
								   PersistentObject persistentObject)
	{
		try
		{
			val.validateUpdate(apiObject, persistentObject,null);
			Assert.fail(String.format("Should have thrown %s",exceptionType.getName()));
		}
		catch (Throwable e)
		{
			if(!exceptionType.isAssignableFrom(e.getClass()))
				Assert.fail(String.format("Should have thrown %s but threw %s",exceptionType.getName(),e.getClass().getName()));

			Assert.assertEquals(message,e.getMessage());
		}
	}
}
