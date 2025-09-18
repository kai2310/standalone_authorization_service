package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.model.data.acm.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class FeatureAccountIdMapTest
{
    private FeatureAccountIdMap idMap;

    @Before
    public void setUp() throws Exception
    {
        idMap = new FeatureAccountIdMap();
    }

    @Test
    public void testBasicOperations()
    {
        idMap.add(build(1L,"account/1",10L,11L));
        idMap.add(build(2L,"account/2",10L,12L));
        idMap.add(build(3L,"account/3",13L));

        Collection<CompoundId> matching = idMap.getAccountIds(10L);
        assertEquals(2,matching.size());

        assertMatches(Util.asSet(CompoundId.build("account/1"),CompoundId.build("account/2")),matching);

        // verify that the underlying maps are in sync
        Map<Long,Set<CompoundId>> featureIdMap = idMap.getFeatureIdMap();
        Assert.assertEquals(4,featureIdMap.size());

        idMap.update(build(1L,"account/1",10L,11L),build(1L,"account/1",10L,14L));
        matching = idMap.getAccountIds(10L);
        assertEquals(2,matching.size());

        assertMatches(Util.asSet(CompoundId.build("account/1"),CompoundId.build("account/2")),matching);

        matching = idMap.getAccountIds(11L);
        assertEquals(0,matching.size());

        matching = idMap.getAccountIds(14L);
        assertEquals(1,matching.size());
        assertMatches(Util.asSet(CompoundId.build("account/1")),matching);

        // verify that the underlying maps are in sync
        featureIdMap = idMap.getFeatureIdMap();
        Assert.assertEquals(4,featureIdMap.size());
        assertNull(featureIdMap.get(11));

        idMap.remove(build(2L,"account/2",10L,12L));
        matching = idMap.getAccountIds(10L);
        assertEquals(1,matching.size());
        matching = idMap.getAccountIds(12L);
        assertEquals(0,matching.size());

        idMap.clear();
        featureIdMap = idMap.getFeatureIdMap();
        Assert.assertEquals(0,featureIdMap.size());
    }

    @Test
    public void testUpdateWithExistingAccountIsNull()
    {
        idMap.add(build(1L, "account/1", 10L, 11L));
        idMap.update(null, build(1L, "account/1", 10L, 14L));
    }

    @Test
    public void testUpdateWithUpdatingAccountIsNull()
    {
        idMap.add(build(1L, "account/1", 10L, 11L));

        idMap.update(build(1L, "account/1", 10L, 11L), null);
    }

    private Account build(Long id, String accountId, Long... featureIds)
    {
        Account account = new Account();
        account.setId(id);
        account.setAccountId(accountId);
        account.setAccountFeatureIds(new HashSet<Long>(Arrays.asList(featureIds)));

        return account;
    }

    private void assertMatches(Collection expected,Collection actual)
    {
        Assert.assertNotNull(expected);
        Assert.assertNotNull(actual);
        Assert.assertTrue(expected.size() == actual.size() && expected.containsAll(actual));
    }
}
