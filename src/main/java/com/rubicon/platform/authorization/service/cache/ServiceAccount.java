package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.model.data.acm.Account;

/**
 */
public class ServiceAccount extends Account
{
    private CompoundId accountKey;

    private ServiceAccount()
    {
    }

    public ServiceAccount(Account other)
    {
        this.setId(other.getId());
        this.setAccountFeatureIds(other.getAccountFeatureIds());
        this.setAccountId(other.getAccountId());
        this.setAccountName(other.getAccountName());
        this.setCreated(other.getCreated());
        this.setModified(other.getModified());
        this.setSource(other.getSource());
        this.setStatus(other.getStatus());

        accountKey = CompoundId.build(getAccountId());
    }

    public CompoundId getAccountKey()
    {
        return accountKey;
    }
}
