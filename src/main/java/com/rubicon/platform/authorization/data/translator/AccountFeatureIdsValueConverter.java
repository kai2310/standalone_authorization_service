package com.rubicon.platform.authorization.data.translator;

import com.dottydingo.hyperion.core.persistence.PersistenceContext;
import com.dottydingo.hyperion.core.translation.ValueConverter;

import java.util.*;

/**
 */
public class AccountFeatureIdsValueConverter implements ValueConverter<Set<Long>,Set<Long>>
{
    @Override
    public Set<Long> convertToClientValue(Set<Long> persistentValue, PersistenceContext context)
    {
        if(persistentValue == null)
            return null;

        // this is a lazy array so this will force the collection to be instantiated
        return new LinkedHashSet<>(persistentValue);
    }

    @Override
    public Set<Long> convertToPersistentValue(Set<Long> clientValue, PersistenceContext context)
    {
        if(clientValue == null)
            return null;

        return new LinkedHashSet<>(clientValue);
    }
}
