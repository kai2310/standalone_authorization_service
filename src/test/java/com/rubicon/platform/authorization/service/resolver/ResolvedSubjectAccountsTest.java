package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.data.model.CompoundId;
import junit.framework.Assert;
import org.junit.Test;

/**
 */
public class ResolvedSubjectAccountsTest
{
    @Test
    public void testGetAccountMatchType_None()
    {
        ResolvedSubjectAccounts accounts = new ResolvedSubjectAccounts();
        Assert.assertEquals(AccountMatchType.NONE,accounts.getAccountMatchType());
    }

    @Test
    public void testGetAccountMatchType_Account()
    {
        ResolvedSubjectAccounts accounts = new ResolvedSubjectAccounts();
        accounts.addAccountMatch(CompoundId.build("account/1"),new OperationMatchResult());
        Assert.assertEquals(AccountMatchType.ACCOUNT,accounts.getAccountMatchType());
    }

    @Test
    public void testGetAccountMatchType_AccountGroup()
    {
        ResolvedSubjectAccounts accounts = new ResolvedSubjectAccounts();
        accounts.addAccountGroupMatch(10L, new OperationMatchResult());
        Assert.assertEquals(AccountMatchType.ACCOUNT_GROUP,accounts.getAccountMatchType());
    }

    @Test
    public void testGetAccountMatchType_Mixed()
    {
        ResolvedSubjectAccounts accounts = new ResolvedSubjectAccounts();
        accounts.addAccountMatch(CompoundId.build("account/1"),new OperationMatchResult());
        accounts.addAccountGroupMatch(10L,new OperationMatchResult());
        Assert.assertEquals(AccountMatchType.MIXED,accounts.getAccountMatchType());
    }
}
