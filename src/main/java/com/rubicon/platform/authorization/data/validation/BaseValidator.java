package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.api.exception.ValidationException;
import com.dottydingo.hyperion.core.model.PersistentObject;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.validation.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * User: mhellkamp
 * Date: 9/12/12
 */
public abstract class BaseValidator<C extends ApiObject,P extends PersistentObject> implements Validator<C,P>
{
    private static final Pattern UNICODE_PATTERN = Pattern.compile("[^\\u0000-\\u007F]+");

	protected void assertRequired(String fieldName, String value)
	{
		if(value == null || value.trim().length() == 0)
			throw new ValidationException(String.format("A value must be specified for %s",fieldName));
	}

	protected void assertNotBlank(String fieldName, String value)
	{
		if(value != null && value.trim().length() == 0)
			throw new ValidationException(String.format("A value must be specified for %s", fieldName));
	}

	protected void assertRequired(String fieldName, Object value)
	{
		if(value == null)
			throw new ValidationException(String.format("A value must be specified for %s",fieldName));
	}


	protected void assertNoDuplicates(String fieldName,List<String> list)
	{
		if(list == null || list.size() == 0)
			return;

		Set<String> set = new HashSet<String>();
		for (String item : list)
		{
			if(!set.add(item))
				throw new ValidationException(String.format("The field \"%s\" contains duplicate values.",fieldName));
		}
	}

    protected void assertNotChanged(String fieldName, Object clientValue,Object persistentValue)
    {
        if(clientValue != null && !clientValue.equals(persistentValue))
            throw new ValidationException(String.format("The value for \"%s\" can not be changed.", fieldName));
    }

    protected void validateLength(String fieldName, String value, int maxLength)
    {
        if(value != null && value.length() > maxLength)
            throw new ValidationException(
                    String.format("The value for \"%s\" can not be longer than %d characters.", fieldName,maxLength));
    }

	protected void assertReadOnly(String fieldName, Object value)
	{
		if(value != null)
			throw new ValidationException(String.format("The field %s is read only and can not be specified on create.",fieldName));
	}

    protected boolean valueChanged(Object clientValue, Object persistentValue)
    {
        return clientValue != null && !clientValue.equals(persistentValue);
    }
	protected <T> T resolveValue(T client, T persistent)
	{
		if(client != null)
			return client;
		return persistent;
	}

    protected boolean containsUnicode(String value)
    {
        if(value == null)
            return false;

        return UNICODE_PATTERN.matcher(value).find();
    }

    protected void assertLatin(String fieldName, String value)
    {
        if(containsUnicode(value))
            throw new ValidationException(String.format("The field \"%s\" may not contain unicode characters.",fieldName));
    }

    protected void assertLatin(String fieldName, List<String> values)
    {
        if(values != null && values.size() > 0)
        {
            for (String value : values)
            {
                assertLatin(fieldName,value);
            }
        }
    }

	@Override
	public void validateDelete(P persistentObject,PersistenceContext context)
	{

	}
}
