package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.ValueConverter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 */
public class AccountIdsValueConverter implements ValueConverter<List<Long>,List<Long>>
{
    @Override
    public List<Long> convertToClientValue(List<Long> persistentValue, PersistenceContext context)
    {
        if(persistentValue == null)
            return null;

        return new ArrayList<>(persistentValue);
    }

    @Override
    public List<Long> convertToPersistentValue(List<Long> clientValue, PersistenceContext context)
    {
        if(clientValue == null)
            return null;

        // remove duplicates
        Set<Long> unique = new LinkedHashSet<>(clientValue);
        return new ArrayList<>(unique);
    }
}
