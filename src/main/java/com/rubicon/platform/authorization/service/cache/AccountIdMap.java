package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.translator.IdParser;
import com.rubicon.platform.authorization.model.data.acm.Account;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class AccountIdMap
{
    private static final Collection<CompoundId> EMPTY = Collections.emptyList();

    private IdParser idParser = IdParser.STANDARD_ID_PARSER;
    private Map<CompoundId,Long> idMap = new ConcurrentHashMap<>();
    private Map<String,Set<CompoundId>> accountTypeMap = new ConcurrentHashMap<>();

    public Collection<CompoundId> getMatching(String idType)
    {
        Set<CompoundId> keys = accountTypeMap.get(idType);
        if(keys == null) return EMPTY;
        return Collections.unmodifiableCollection(keys);
    }

    public Long getId(CompoundId accountId)
    {
        return idMap.get(accountId);
    }

    public synchronized void add(Account account)
    {
        CompoundId accountId = idParser.parseId(account.getAccountId());
        idMap.put(accountId,account.getId());

        // only store active entries in the account type map
        if(!"active".equalsIgnoreCase(account.getStatus()))
            return;

        addTypeEntry(accountId);
    }

    private void addTypeEntry(CompoundId accountId)
    {
        Set<CompoundId> newIds = null;
        Set<CompoundId> ids = accountTypeMap.get(accountId.getIdType());
        if(ids == null)
        {
            newIds = new HashSet<>();
        }
        else
            newIds = new HashSet<>(ids);

        newIds.add(accountId);
        accountTypeMap.put(accountId.getIdType(),newIds);
    }

    public synchronized void update(Account account)
    {
        CompoundId accountId = idParser.parseId(account.getAccountId());

        // only store active entries in the account type map
        if ("active".equalsIgnoreCase(account.getStatus()))
        {
            addTypeEntry(accountId);
            // idMap doesn't have entry for a deleted account
            // after this deleted account is reactivated, we need to also add it into idMap
            idMap.putIfAbsent(accountId, account.getId());
        }
        else
        {
            removeTypeEntry(accountId);
        }
    }

    public synchronized void remove(Account account)
    {
        CompoundId accountId = idParser.parseId(account.getAccountId());
        idMap.remove(accountId);
        removeTypeEntry(accountId);
    }

    private void removeTypeEntry(CompoundId accountId)
    {
        Set<CompoundId> ids = accountTypeMap.get(accountId.getIdType());
        if(ids != null)
        {
            Set<CompoundId> newIds = new HashSet<>(ids);
            newIds.remove(accountId);
            if(newIds.isEmpty())
                accountTypeMap.remove(accountId.getIdType());
            else
                accountTypeMap.put(accountId.getIdType(),newIds); // to avoid concurrent modification if someone is looking at the existing one

        }
    }

    public synchronized void clear()
    {
        idMap.clear();
        accountTypeMap.clear();
    }

    // testing only
    protected Map<CompoundId, Long> getIdMap()
    {
        return Collections.unmodifiableMap(idMap);
    }

    // testing only
    protected Map<String, Set<CompoundId>> getAccountTypeMap()
    {
        return Collections.unmodifiableMap(accountTypeMap);
    }
}
