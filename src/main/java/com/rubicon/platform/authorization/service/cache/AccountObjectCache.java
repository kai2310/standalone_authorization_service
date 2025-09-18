package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.model.data.acm.Account;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * User: mhellkamp
 * Date: 10/24/12
 */
public class AccountObjectCache extends ObjectCache<Account, Long, Long>
{

    private AccountIdMap accountIdMap = new AccountIdMap();
    private FeatureAccountIdMap featureAccountIdMap = new FeatureAccountIdMap();

    public AccountObjectCache(Ehcache cache)
    {
        super(cache);
    }

    public Collection<CompoundId> getMatching(CompoundId compoundId)
    {
        return accountIdMap.getMatching(compoundId.getIdType());
    }

    public Collection<CompoundId> getMatching(String accountType)
    {
        return accountIdMap.getMatching(accountType);
    }


    public Collection<CompoundId> getForFeatureId(Long featureId)
    {
        return featureAccountIdMap.getAccountIds(featureId);
    }

    public Account getByAccountId(CompoundId accountId)
    {
        Long id = accountIdMap.getId(accountId);
        if (id != null)
        {
            return getItemById(id);
        }
        return null;
    }

    public boolean getByAccountId(String contextType, String contextId)
    {
        Account account = getByAccountId(new CompoundId(contextType, contextId));
        return (account != null);
    }


    public List<CompoundId> getAccountIds(Collection<Long> ids)
    {
        List<CompoundId> compoundIds = new LinkedList<>();
        for (Long id : ids)
        {
            ServiceAccount account = (ServiceAccount) getItemById(id);
            if (account != null)
            {
                compoundIds.add(account.getAccountKey());
            }
        }

        return compoundIds;
    }

    @Override
    protected Serializable generateKey(Account entry)
    {
        return entry.getId();
    }

    @Override
    public void addEntry(Account entry)
    {
        if ("deleted".equalsIgnoreCase(entry.getStatus()))
        {
            return;
        }

        Long key = (Long) generateKey(entry);
        synchronized (cache)
        {
            cache.put(createElement(key, entry));
            accountIdMap.add(entry);
            featureAccountIdMap.add(entry);
        }
    }

    @Override
	public void removeEntry(Long id)
	{
        synchronized (cache)
        {
			Account account = getItemById(id);
		    if(account != null)
			{
                synchronized (cache)
                {
                    cache.remove(id);
                    accountIdMap.remove(account);
                    featureAccountIdMap.remove(account);
                }
			}
        }
	}

	@Override
	public void updateEntry(Account entry)
	{
		if("deleted".equalsIgnoreCase(entry.getStatus()))
		{
			removeEntry(entry.getId());
		}
		else
		{
            synchronized (cache)
            {
                Account existing = getItemById(entry.getId());
                super.updateEntry(entry);
                accountIdMap.update(entry);
                featureAccountIdMap.update(existing,entry);
            }
		}
	}

	@Override
	protected Element createElement(Serializable key, Serializable entry)
	{
		return new Element(key,new KryoWrapper(new ServiceAccount((Account) entry)));
	}

	@Override
    public void clear()
    {
        synchronized (cache)
        {
            accountIdMap.clear();
            featureAccountIdMap.clear();
            cache.removeAll();
        }
    }
}
