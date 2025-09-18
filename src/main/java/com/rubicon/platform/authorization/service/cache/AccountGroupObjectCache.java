package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.model.data.acm.AccountGroup;
import net.sf.ehcache.Ehcache;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 */
public class AccountGroupObjectCache extends ObjectCache<AccountGroup,Long,Long>
{
    private static final Set<Long> EMPTY = Collections.emptySet();

    private AccountGroupIdMap idMap = new AccountGroupIdMap();
    private Map<String,Set<Long>> accountTypeMap = new ConcurrentHashMap<>();

    public AccountGroupObjectCache(Ehcache cache)
    {
        super(cache);
    }

    public Set<Long> getAccountGroupIdsForAccount(Long accountId)
    {
        return idMap.getAccountGroupsForAccount(accountId);
    }

    public Set<Long> getAccountGroupsForType(String idType)
    {
        Set<Long> ids = accountTypeMap.get(idType);
        if(ids == null) return EMPTY;
        return Collections.unmodifiableSet(ids);
    }

    @Override
    public void addEntry(AccountGroup entry)
    {
        Long key = (Long) generateKey(entry);
        synchronized (cache)
        {
            cache.put(createElement(key, entry));
            idMap.add(entry.getId(),entry.getAccountIds());
            if(StringUtils.isNotEmpty(entry.getAccountType()))
            {
                Set<Long> newIds = null;
                Set<Long> ids = accountTypeMap.get(entry.getAccountType());
                if(ids == null)
                    newIds = new HashSet<>();
                else
                    newIds = new HashSet<>(ids);
                newIds.add(entry.getId());
                accountTypeMap.put(entry.getAccountType(),newIds);
            }
        }
    }

    @Override
    public void updateEntry(AccountGroup entry)
    {
        synchronized (cache)
        {
            AccountGroup existing = getItemById(entry.getId());
            cache.put(createElement(entry.getId(), entry));

            List<Long> exitingAccountIds = existing != null ? existing.getAccountIds() : null;
            idMap.update(entry.getId(),exitingAccountIds,entry.getAccountIds());
        }
    }

    @Override
    public void removeEntry(Long id)
    {
        synchronized (cache)
        {
            AccountGroup existing = getItemById(id);
            if(existing != null)
            {
                cache.remove(id);
                idMap.remove(id,existing.getAccountIds());
                if(StringUtils.isNotEmpty(existing.getAccountType()))
                {
                    Set<Long> ids = accountTypeMap.get(existing.getAccountType());
                    if(id != null)
                    {
                        Set<Long> newIds = new HashSet<>(ids);
                        newIds.remove(existing.getId());
                        if(newIds.isEmpty())
                            accountTypeMap.remove(existing.getAccountType());
                    }
                }
            }
        }
    }

    @Override
    public void clear()
    {
        synchronized (cache)
        {
            idMap.clear();
            cache.removeAll();
        }
    }


}
