package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.model.data.acm.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 */
public class AccountIdMapTest
{
    private AccountIdMap idMap;

    @Before
    public void setup()
    {
        idMap = new AccountIdMap();
    }

    @Test
    public void testBasicOperations()
    {
        idMap.add(build(1L,"account/1"));
        idMap.add(build(2L,"account/2"));
        idMap.add(build(3L,"other/1"));


        Assert.assertEquals(new Long(1L),idMap.getId(CompoundId.build("account/1")));
        Assert.assertEquals(new Long(2L),idMap.getId(CompoundId.build("account/2")));
        Assert.assertEquals(new Long(3L),idMap.getId(CompoundId.build("other/1")));

        Collection<CompoundId> matching = idMap.getMatching("account");
        Assert.assertEquals(2,matching.size());
        assertEquals(Util.asSet(CompoundId.build("account/1"), CompoundId.build("account/2")), matching);

        matching = idMap.getMatching("other");
        Assert.assertEquals(1,matching.size());
        assertEquals(Util.asSet(CompoundId.build("other/1")),matching);

        matching = idMap.getMatching("foo");
        Assert.assertEquals(0,matching.size());


        // verify that the underlying maps are in sync
        Map<CompoundId,Long> keyMap = idMap.getIdMap();
        Assert.assertEquals(3,keyMap.size());
        Assert.assertEquals(new Long(1L),keyMap.get(CompoundId.build("account/1")));
        Assert.assertEquals(new Long(2L),keyMap.get(CompoundId.build("account/2")));
        Assert.assertEquals(new Long(3L),keyMap.get(CompoundId.build("other/1")));

        Map<String,Set<CompoundId>> accountTypeMap = idMap.getAccountTypeMap();
        Assert.assertEquals(2,accountTypeMap.size());

        assertEquals(Util.asSet(CompoundId.build("account/1"), CompoundId.build("account/2")),accountTypeMap.get("account"));
        assertEquals(Util.asSet(CompoundId.build("other/1")),accountTypeMap.get("other"));

        // delete
        idMap.remove(build(1L,"account/1"));
        idMap.remove(build(3L,"other/1"));

        Assert.assertNull(idMap.getId(CompoundId.build("account/1")));
        Assert.assertEquals(new Long(2L),idMap.getId(CompoundId.build("account/2")));
        Assert.assertNull(idMap.getId(CompoundId.build("other/1")));

        matching = idMap.getMatching("account");
        Assert.assertEquals(1,matching.size());
        assertEquals(Util.asSet(CompoundId.build("account/2")), matching);

        matching = idMap.getMatching("other");
        Assert.assertEquals(0,matching.size());

        // verify that the underlying maps are in sync
        keyMap = idMap.getIdMap();
        Assert.assertEquals(1,keyMap.size());
        Assert.assertEquals(new Long(2L),keyMap.get(CompoundId.build("account/2")));

        accountTypeMap = idMap.getAccountTypeMap();
        Assert.assertEquals(1,accountTypeMap.size());

        assertEquals(Util.asSet(CompoundId.build("account/2")),accountTypeMap.get("account"));

        idMap.clear();
        keyMap = idMap.getIdMap();
        Assert.assertEquals(0,keyMap.size());
        accountTypeMap = idMap.getAccountTypeMap();
        Assert.assertEquals(0,accountTypeMap.size());
    }

    @Test
    public void testStatus()
    {
        idMap.add(build(1L,"account/1"));
        idMap.add(build(2L,"account/2","pending"));


        Assert.assertEquals(new Long(1L),idMap.getId(CompoundId.build("account/1")));
        Assert.assertEquals(new Long(2L),idMap.getId(CompoundId.build("account/2")));

        Collection<CompoundId> matching = idMap.getMatching("account");
        Assert.assertEquals(1,matching.size());
        assertEquals(Util.asSet(CompoundId.build("account/1")), matching);

        // verify that the underlying maps are in sync
        Map<String,Set<CompoundId>> accountTypeMap = idMap.getAccountTypeMap();
        Assert.assertEquals(1,accountTypeMap.size());

        assertEquals(Util.asSet(CompoundId.build("account/1")),accountTypeMap.get("account"));

        idMap.update(build(1L,"account/1","pending"));
        idMap.update(build(2L,"account/2"));

        Assert.assertEquals(new Long(1L),idMap.getId(CompoundId.build("account/1")));
        Assert.assertEquals(new Long(2L),idMap.getId(CompoundId.build("account/2")));

        matching = idMap.getMatching("account");
        Assert.assertEquals(1,matching.size());
        assertEquals(Util.asSet(CompoundId.build("account/2")), matching);

        // delete
        idMap.remove(build(1L,"account/2"));

        Assert.assertNull(idMap.getId(CompoundId.build("account/2")));
        Assert.assertEquals(new Long(1L),idMap.getId(CompoundId.build("account/1")));

        accountTypeMap = idMap.getAccountTypeMap();
        Assert.assertEquals(0, accountTypeMap.size());

    }

    private Account build(Long id,String accountId)
    {
        return build(id, accountId,"active");
    }

    private Account build(Long id,String accountId,String status)
    {
        Account account = new Account();
        account.setId(id);
        account.setAccountId(accountId);
        account.setStatus(status);

        return account;
    }

    private void assertEquals(Collection expected,Collection actual)
    {
        Assert.assertNotNull(expected);
        Assert.assertNotNull(actual);
        Assert.assertTrue(expected.size() == actual.size() && expected.containsAll(actual));
    }
}
