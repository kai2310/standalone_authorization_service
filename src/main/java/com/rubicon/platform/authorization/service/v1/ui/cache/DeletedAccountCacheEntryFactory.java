package com.rubicon.platform.authorization.service.v1.ui.cache;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletedAccountCacheEntryFactory implements CacheEntryFactory
{
    private static Logger logger = LoggerFactory.getLogger(DeletedAccountCacheEntryFactory.class);
    private AccountLoader accountLoader;

    @Override
    public Object createEntry(Object key) throws Exception
    {
        try
        {
            PersistentAccount persistentAccount = accountLoader.findByAccountId((CompoundId) key);
            com.rubicon.platform.authorization.model.data.acm.Account account =
                    new com.rubicon.platform.authorization.model.data.acm.Account();
            account.setAccountName(persistentAccount.getAccountName());
            account.setId(persistentAccount.getId());

            return account;
        }
        catch (Exception e)
        {
            logger.error(String.format("Error retrieving account for %s", key), e);
        }

        return null;
    }

    public void setAccountLoader(AccountLoader accountLoader)
    {
        this.accountLoader = accountLoader;
    }
}
