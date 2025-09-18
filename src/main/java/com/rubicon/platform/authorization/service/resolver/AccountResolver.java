package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.service.cache.AccountGroupObjectCache;
import com.rubicon.platform.authorization.service.cache.AccountObjectCache;
import com.rubicon.platform.authorization.model.data.acm.AccountGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

/**
 */
@Component
public class AccountResolver
{
    private Logger logger = LoggerFactory.getLogger(AccountResolver.class);

    @Autowired
    private AccountObjectCache accountCache;

    @Autowired
    private AccountGroupObjectCache accountGroupCache;

    public Collection<CompoundId> resolveAccountIds(Long accountGroupId)
    {
        AccountGroup accountGroup = accountGroupCache.getItemById(accountGroupId);
        if(accountGroup == null)
        {
            logger.warn("Could not find AccountGroup ID: {}",accountGroupId);
            return Collections.emptyList();
        }

        if(StringUtils.isNotEmpty(accountGroup.getAccountType()))
            return accountCache.getMatching(accountGroup.getAccountType());

        return accountCache.getAccountIds(accountGroup.getAccountIds());
    }
}
