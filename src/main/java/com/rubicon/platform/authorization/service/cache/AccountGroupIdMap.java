package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.model.data.acm.AccountGroup;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class AccountGroupIdMap
{
    private static final Set<Long> EMPTY = Collections.emptySet();

    private Map<Long,Set<Long>> accountMap = new ConcurrentHashMap<>();

    public Set<Long> getAccountGroupsForAccount(Long accountId)
    {
        Set<Long> ids = accountMap.get(accountId);
        if(ids == null) return EMPTY;
        return Collections.unmodifiableSet(ids);

    }
    public synchronized void add(Long accountGroupId, Collection<Long> accountIds)
    {
        if(accountIds != null)
        {
            for (Long accountId : accountIds)
            {
                Set<Long> ids = accountMap.get(accountId);
                if (ids == null)
                {
                    ids = new HashSet<>();
                    accountMap.put(accountId, ids);
                }
                ids.add(accountGroupId);
            }
        }
    }

    public synchronized void remove(Long accountGroupId, Collection<Long> accountIds)
    {
        if(accountIds != null)
        {
            for (Long accountId : accountIds)
            {
                Set<Long> ids = accountMap.get(accountId);
                if (ids != null)
                {
                    Set<Long> newIds = new HashSet<>(ids);
                    newIds.remove(accountGroupId);
                    if (newIds.isEmpty())
                        accountMap.remove(accountId);
                    else
                        accountMap.put(accountId,
                                newIds);   // to avoid concurrent modification if someone is looking at the existing one
                }
            }
        }
    }

    public synchronized void update(Long accountGroupId, List<Long> oldEntry, List<Long>  updatedEntry)
    {
        Set<Long> existing = buildSet(oldEntry);
        Set<Long> updated = buildSet(updatedEntry);

        // new entries
        updated.removeAll(getList(oldEntry));

        // removed entries
        existing.removeAll(getList(updatedEntry));

        add(accountGroupId,updated);
        remove(accountGroupId,existing);

    }

    public synchronized void clear()
    {
        accountMap.clear();
    }

    private List<Long> getList(List<Long> list)
    {
        if(list == null)
            Collections.emptyList();
        return list;
    }
    private Set<Long> buildSet(List<Long> list)
    {
        if(list == null)
            return new HashSet<>();
        return new HashSet<>(list);
    }
}
