package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.translator.IdParser;
import com.rubicon.platform.authorization.model.data.acm.Account;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class FeatureAccountIdMap
{
    private IdParser idParser = IdParser.STANDARD_ID_PARSER;
    private static final Collection<CompoundId> EMPTY = Collections.emptyList();
    private Map<Long,Set<CompoundId>> featureIdMap = new ConcurrentHashMap<>();

    public Collection<CompoundId> getAccountIds(Collection<Long> featureIds)
    {
        Set<CompoundId> ids = new HashSet<>();
        for (Long featureId : featureIds)
        {
            ids.addAll(getAccountIds(featureId));
        }

        return ids;
    }

    public Collection<CompoundId> getAccountIds(Long featureId)
    {
        Set<CompoundId> ids = featureIdMap.get(featureId);
        if(ids == null) return new HashSet<>();
        return new HashSet<>(ids);
    }

    public synchronized void add(Account account)
    {
        if(account.getAccountFeatureIds() == null) return;

        CompoundId accountId = idParser.parseId(account.getAccountId());
        for (Long id : account.getAccountFeatureIds())
        {
            addEntry(id,accountId);
        }
    }

    public synchronized void remove(Account account)
    {
        if(account.getAccountFeatureIds() == null) return;

        CompoundId accountId = idParser.parseId(account.getAccountId());
        for (Long id : account.getAccountFeatureIds())
        {
            removeEntry(id,accountId);
        }
    }

    public synchronized void update(Account existingAccount, Account updatedAccount)
    {
        Set<Long> existingFeatures = new HashSet<>();
        if (existingAccount != null && existingAccount.getAccountFeatureIds() != null)
            existingFeatures.addAll(existingAccount.getAccountFeatureIds());

        Set<Long> newFeatures = new HashSet<>();
        if (updatedAccount != null && updatedAccount.getAccountFeatureIds() != null)
            newFeatures.addAll(updatedAccount.getAccountFeatureIds());

        Set<Long> toAdd = new HashSet<>(newFeatures);
        toAdd.removeAll(existingFeatures);

        Set<Long> toRemove = new HashSet<>(existingFeatures);
        toRemove.removeAll(newFeatures);

        if(toAdd.isEmpty() && toRemove.isEmpty())
            return;

        if (updatedAccount != null)
        {
            CompoundId accountId = idParser.parseId(updatedAccount.getAccountId());
            for (Long id : toAdd)
            {
                addEntry(id, accountId);
            }

            for (Long id : toRemove)
            {
                removeEntry(id, accountId);
            }
        }
    }

    public synchronized void clear()
    {
        featureIdMap.clear();
    }

    private void addEntry(Long featureId, CompoundId accountId)
    {
        Set<CompoundId> newIds = null;
        Set<CompoundId> ids = featureIdMap.get(featureId);
        if(ids == null)
            newIds = new HashSet<>();
        else
            newIds = new HashSet<>(ids);

        newIds.add(accountId);
        featureIdMap.put(featureId,newIds);

    }

    private void removeEntry(Long featureId, CompoundId accountId)
    {
        Set<CompoundId> ids = featureIdMap.get(featureId);
        if(ids != null)
        {
            Set<CompoundId> newIds = new HashSet<>(ids);
            newIds.remove(accountId);
            if(newIds.isEmpty())
                featureIdMap.remove(featureId);
            else
                featureIdMap.put(featureId,newIds);
        }

    }

    // testing purposes only
    Map<Long, Set<CompoundId>> getFeatureIdMap()
    {
        return featureIdMap;
    }
}
