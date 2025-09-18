package com.rubicon.platform.authorization.service.jobs;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccount;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: mhellkamp
 * Date: 10/24/12
 */
public class AccountUpdateWorker
{
    private static Logger logger = LoggerFactory.getLogger(AccountUpdateWorker.class);
    private AccountLoader accountLoader;
    private List<String> publisherAutoAddFeatureIds;

    public void setAccountLoader(AccountLoader accountLoader)
    {
        this.accountLoader = accountLoader;
    }

    public void setPublisherAutoAddFeatureIds(List<String> publisherAutoAddFeatureIds)
    {
        this.publisherAutoAddFeatureIds = publisherAutoAddFeatureIds;
    }

    @Transactional(propagation = Propagation.REQUIRED,readOnly = false)
    public AccountUpdateResult process(RevvAccount account, String accountType)
    {
        Status status = null;
        Date date = new Date();
        PersistentAccount persistentAccount
                = accountLoader.findByAccountId(new CompoundId(accountType, account.getId()));
        if (persistentAccount == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(
                        String.format("New %s ID:%s Name:\"%s\"", accountType, account.getId(), account.getLabel()));
            }
            persistentAccount = new PersistentAccount();
            persistentAccount.setAccountName(account.getLabel());
            //persistentAccount.setAccountType(accountType);
            persistentAccount.setAccountId(new CompoundId(accountType, account.getId()));
            persistentAccount.setCreated(date);
            persistentAccount.setModified(date);
            persistentAccount.setStatus(account.getStatus());
            persistentAccount.setSource("revv");
            // when creating new publisher, auto add certain features
            if (accountType.equals(CompoundId.PUBLISHER))
            {
                // not using stream due to an issue when starting up the service
                // https://stackoverflow.com/questions/41724887/why-javaassist-throws-invalid-constant-type-18-when-loading-entitymanager-only
                Set<Long> accountFeatureIds = new HashSet<>();
                for(String autoAddFeatureId : publisherAutoAddFeatureIds)
                {
                    accountFeatureIds.add(Long.valueOf(autoAddFeatureId));
                }
                persistentAccount.setAccountFeatureIds(accountFeatureIds);
            }
            status = Status.created;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(String.format("Updated %s ID:%s Name:\"%s\"", accountType, account.getId(),
                        account.getLabel()));
            }

            // If the persistent object is deleted, but the existing object is not deleted, we want to set the status to
            // create so the caching of the objects works correctly.
            status = (persistentAccount.getStatus().equals("deleted") && !account.getStatus().equals("deleted"))
                     ?
                     Status.created
                     : Status.updated;

            persistentAccount.setAccountName(account.getLabel());
            persistentAccount.setStatus(account.getStatus());
            persistentAccount.setModified(date);
            persistentAccount.setSource("revv");

        }

        persistentAccount = accountLoader.save(persistentAccount);

        return new AccountUpdateResult(persistentAccount.getId(), status);
	}
}
