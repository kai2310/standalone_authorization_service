package com.rubicon.platform.authorization.data.translator;

import com.rubicon.platform.authorization.data.model.PersistentAccountGroupType;
import com.rubicon.platform.authorization.model.data.acm.AccountGroupType;

/**
 */
public class AccountGroupTypeTranslator extends BaseStatusTranslator<AccountGroupType,PersistentAccountGroupType>
{
    public AccountGroupTypeTranslator()
    {
        super(AccountGroupType.class, PersistentAccountGroupType.class);
    }
}
