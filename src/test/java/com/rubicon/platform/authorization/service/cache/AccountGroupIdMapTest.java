package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.Util;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

/**
 */
public class AccountGroupIdMapTest
{
    private AccountGroupIdMap idMap;

    @Before
    public void setup()
    {
        idMap = new AccountGroupIdMap();
    }

    @Test
    public void testAdd()
    {
        idMap.add(1L, Util.asList(10L,20L,30L));
        idMap.add(2L, Util.asList(20L,10L,40L));
        idMap.add(3L, Util.asList(50L,60L));
        idMap.add(4L, Util.asList(10L,60L));

        Set<Long> groups = idMap.getAccountGroupsForAccount(10L);
        assertEquals(Util.asList(1L,2L,4L),groups);

        groups = idMap.getAccountGroupsForAccount(20L);
        assertEquals(Util.asList(1L,2L),groups);

        groups = idMap.getAccountGroupsForAccount(30L);
        assertEquals(Util.asList(1L),groups);

        groups = idMap.getAccountGroupsForAccount(40L);
        assertEquals(Util.asList(2L),groups);

        groups = idMap.getAccountGroupsForAccount(50L);
        assertEquals(Util.asList(3L),groups);

        groups = idMap.getAccountGroupsForAccount(60L);
        assertEquals(Util.asList(3L,4L),groups);

    }

    @Test
    public void testRemove()
    {
        idMap.add(1L, Util.asList(10L,20L,30L));
        idMap.add(2L, Util.asList(20L,10L,40L));
        idMap.add(3L, Util.asList(50L,60L));
        idMap.add(4L, Util.asList(10L,60L));

        idMap.remove(2L,Util.asList(20L,10L,40L));

        Set<Long> groups = idMap.getAccountGroupsForAccount(10L);
        assertEquals(Util.asList(1L,4L),groups);

        groups = idMap.getAccountGroupsForAccount(20L);
        assertEquals(Util.asList(1L),groups);

        groups = idMap.getAccountGroupsForAccount(30L);
        assertEquals(Util.asList(1L),groups);

        groups = idMap.getAccountGroupsForAccount(40L);
        Assert.assertEquals(0,groups.size());

        groups = idMap.getAccountGroupsForAccount(50L);
        assertEquals(Util.asList(3L),groups);

        groups = idMap.getAccountGroupsForAccount(60L);
        assertEquals(Util.asList(3L,4L),groups);

    }

    @Test
    public void testUpdate()
    {
        idMap.add(1L, Util.asList(10L,20L,30L));
        idMap.add(2L, Util.asList(20L,10L,40L));
        idMap.add(3L, Util.asList(50L,60L));
        idMap.add(4L, Util.asList(10L,60L));

        idMap.update(1L,Util.asList(10L,20L,30L),Util.asList(10L,70L));

        Set<Long> groups = idMap.getAccountGroupsForAccount(10L);
        assertEquals(Util.asList(1L,2L,4L),groups);

        groups = idMap.getAccountGroupsForAccount(20L);
        assertEquals(Util.asList(2L),groups);

        groups = idMap.getAccountGroupsForAccount(30L);
        Assert.assertEquals(0,groups.size());

        groups = idMap.getAccountGroupsForAccount(40L);
        assertEquals(Util.asList(2L),groups);

        groups = idMap.getAccountGroupsForAccount(50L);
        assertEquals(Util.asList(3L),groups);

        groups = idMap.getAccountGroupsForAccount(60L);
        assertEquals(Util.asList(3L,4L),groups);

        groups = idMap.getAccountGroupsForAccount(70L);
        assertEquals(Util.asList(1L),groups);

    }

    private void assertEquals(Collection expected, Collection actual)
    {
        Assert.assertNotNull(expected);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.size(),actual.size());
        Assert.assertTrue(expected.containsAll(actual));
    }
}
