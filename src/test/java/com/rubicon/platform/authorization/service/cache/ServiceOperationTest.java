package com.rubicon.platform.authorization.service.cache;

import com.rubicon.platform.authorization.model.data.acm.Operation;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceOperationTest
{

    @Test
    public void testMatchResource_Standard() throws Exception
    {
        ServiceOperation operation = build("service","/role-assignments/:role_assignment_id","action");
        Assert.assertFalse(operation.isWildcardResource());

        Assert.assertTrue(operation.matchResource("/role-assignments/:role_assignment_id"));
        Assert.assertFalse(operation.matchResource("/role-assignments/:role_assignment_ids"));
        Assert.assertFalse(operation.matchResource("/role-assignments/1"));
        Assert.assertFalse(operation.matchResource("/role-assignments/*"));
    }

    @Test
    public void testMatchResource_Wildcard() throws Exception
    {
        ServiceOperation operation = build("service","*","action");
        Assert.assertTrue(operation.isWildcardResource());

        Assert.assertTrue(operation.matchResource("resource"));
        Assert.assertTrue(operation.matchResource("resources"));
        Assert.assertTrue(operation.matchResource("resourc"));
        Assert.assertTrue(operation.matchResource("res*"));
    }

    @Test
    public void testMatchResource_Regex() throws Exception
    {
        ServiceOperation operation = build("service","||FOO||BAR||BUZZ||","action");
        Assert.assertFalse(operation.isWildcardResource());

        Assert.assertTrue(operation.matchResource("foo"));
        Assert.assertTrue(operation.matchResource("bar"));
        Assert.assertTrue(operation.matchResource("buzz"));

        Assert.assertFalse(operation.matchResource("foobar"));
        Assert.assertFalse(operation.matchResource("barbuzz"));
        Assert.assertFalse(operation.matchResource("buz"));

    }

    @Test
    public void testMatchAction_Standard() throws Exception
    {
        ServiceOperation operation = build("service","resource","action");
        Assert.assertFalse(operation.isWildcardAction());

        Assert.assertTrue(operation.matchAction("action"));
        Assert.assertFalse(operation.matchAction("actions"));
        Assert.assertFalse(operation.matchAction("actio"));
        Assert.assertFalse(operation.matchAction("act*"));
    }

    @Test
    public void testMatchAction_Wildcard() throws Exception
    {
        ServiceOperation operation = build("service","foo","*");
        Assert.assertTrue(operation.isWildcardAction());

        Assert.assertTrue(operation.matchAction("action"));
        Assert.assertTrue(operation.matchAction("actions"));
        Assert.assertTrue(operation.matchAction("actio"));
        Assert.assertTrue(operation.matchAction("act*"));
    }

    @Test
    public void testMatchAction_Regex() throws Exception
    {
        ServiceOperation operation = build("service","resource","||FOO||BAR||BUZZ||");
        Assert.assertFalse(operation.isWildcardAction());
        Assert.assertTrue(operation.matchAction("foo"));
        Assert.assertTrue(operation.matchAction("bar"));
        Assert.assertTrue(operation.matchAction("buzz"));

        Assert.assertFalse(operation.matchAction("foobar"));
        Assert.assertFalse(operation.matchAction("barbuzz"));
        Assert.assertFalse(operation.matchAction("buz"));

    }


    private ServiceOperation build(String service,String resource,String action)
    {
        return new ServiceOperation(new Operation(service,resource,action));
    }
}