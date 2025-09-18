package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.api.ApiObject;
import com.dottydingo.hyperion.core.model.PersistentObject;
import com.dottydingo.hyperion.core.validation.DefaultValidator;
import com.dottydingo.hyperion.core.validation.ValidationErrorContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 */
public class BaseFrameworkValidator<C extends ApiObject,P extends PersistentObject> extends DefaultValidator<C,P>
{
    private static final Pattern UNICODE_PATTERN = Pattern.compile("[^\\u0000-\\u007F]+");

    public static final String DELETE_CONFLICT="DELETE_CONFLICT";
    public static final String NOT_FOUND="NOT_FOUND";
    public static final String NOT_FOUND_IDS="NOT_FOUND_IDS";
    public static final String NOT_UNIQUE="NOT_UNIQUE";
    public static final String EMPTY="EMPTY";
    public static final String READ_ONLY="READ_ONLY";
    public static final String COLLECTION_NULL_VALUES="COLLECTION_NULL_VALUES";
    public static final String UNICODE_NOT_ALLOWED="UNICODE_NOT_ALLOWED";

    protected void validateNotBlank(ValidationErrorContext errorContext,String fieldName, String value)
    {
        if(value != null && value.trim().length() == 0)
            errorContext.addValidationError(REQUIRED_FIELD, fieldName, fieldName, value);
    }

    protected void validateNotEmpty(ValidationErrorContext errorContext,String fieldName,Collection collection)
    {
        if(collection != null )
        {
            if(collection.size() == 0)
                errorContext.addValidationError(EMPTY, fieldName, fieldName);
            else
            {
                Set unique = new HashSet(collection);
                unique.remove(null);
                if(unique.size() != collection.size())
                    errorContext.addValidationError(COLLECTION_NULL_VALUES, fieldName, fieldName);
            }
        }
    }

    protected void validateReadOnly(ValidationErrorContext errorContext,String fieldName,Object value)
    {
        if(value != null)
            errorContext.addValidationError(READ_ONLY,fieldName,fieldName);
    }


    protected String asString(Object value)
    {
        if(value == null)
            return null;
        return value.toString();
    }

    protected boolean containsUnicode(String value)
    {
        if(value == null)
            return false;

        return UNICODE_PATTERN.matcher(value).find();
    }
}
