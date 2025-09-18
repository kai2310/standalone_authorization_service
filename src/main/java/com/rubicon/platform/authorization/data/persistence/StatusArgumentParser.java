package com.rubicon.platform.authorization.data.persistence;

import com.dottydingo.hyperion.api.exception.BadParameterException;
import com.dottydingo.hyperion.api.exception.HyperionException;
import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.persistence.query.ArgumentParser;
import com.dottydingo.hyperion.core.persistence.query.DefaultArgumentParser;
import com.rubicon.platform.authorization.model.data.acm.Status;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class StatusArgumentParser implements ArgumentParser
{
    @Override
    public <T> T parse(String argument, Class<T> type, PersistenceContext context) throws HyperionException
    {
        try
        {
            return (T) Status.valueOf(argument.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            throw new BadParameterException(createErrorMessage(argument, type, context));
        }

    }

    @Override
    public <T> List<T> parse(List<String> argument, Class<T> type, PersistenceContext context) throws HyperionException
    {
        List<T> results = new ArrayList<>();
        for (String s : argument)
        {
            results.add(parse(s,type, context));
        }
        return results;
    }

    protected <T> String createErrorMessage(String argument, Class<T> type, PersistenceContext context)
    {
        return context.getMessageSource().getErrorMessage(DefaultArgumentParser.PARAMETER_CONVERSION_ERROR,
                context.getLocale(),argument,type);
    }
}
