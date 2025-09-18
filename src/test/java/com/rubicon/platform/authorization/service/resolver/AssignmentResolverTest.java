package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.Util;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 */
public class AssignmentResolverTest
{
    private AssignmentResolver resolver = new AssignmentResolver();

    @Test
    public void testResolveAssignments_AccountOverrideProperty()
    {
        AssignedOperationMatches account = new AssignedOperationMatches();
        account.add(allow(OperationMatch.action));

        AssignedOperationMatches accountGroup = new AssignedOperationMatches();
        accountGroup.add(allow(OperationMatch.action,"foo"));
        accountGroup.add(allow(OperationMatch.service,"bar"));

        assertAllow(resolver.resolveAssignments(account,accountGroup),
                OperationMatch.action);

    }
    @Test
    public void testResolveAssignments_AccountOverrideProperty_merge()
    {
        AssignedOperationMatches account = new AssignedOperationMatches();
        account.add(allow(OperationMatch.action));
        account.add(allow(OperationMatch.action,"buzz"));

        AssignedOperationMatches accountGroup = new AssignedOperationMatches();
        accountGroup.add(allow(OperationMatch.action,"foo"));
        accountGroup.add(allow(OperationMatch.service,"bar"));

        assertAllow(resolver.resolveAssignments(account,accountGroup),
                OperationMatch.action,"buzz");

    }


    @Test
    public void testResolveAssignments_AccountGroupOverrideProperty()
    {
        AssignedOperationMatches account = new AssignedOperationMatches();
        account.add(allow(OperationMatch.service));

        AssignedOperationMatches accountGroup = new AssignedOperationMatches();
        accountGroup.add(allow(OperationMatch.action,"foo"));
        accountGroup.add(allow(OperationMatch.resource,"bar"));

        assertAllow(resolver.resolveAssignments(account,accountGroup),
                OperationMatch.action,"foo","bar");

    }

    @Test
    public void testResolveAssignments_AccountGroupOverrideProperty_merge()
    {
        AssignedOperationMatches account = new AssignedOperationMatches();
        account.add(allow(OperationMatch.service,"buzz"));

        AssignedOperationMatches accountGroup = new AssignedOperationMatches();
        accountGroup.add(allow(OperationMatch.action,"foo"));
        accountGroup.add(allow(OperationMatch.resource,"bar"));

        assertAllow(resolver.resolveAssignments(account,accountGroup),
                OperationMatch.action,"foo","bar","buzz");

    }

    @Test
    public void testResolveAssignments_AccountOverrideDeny()
    {
        AssignedOperationMatches account = new AssignedOperationMatches();
        account.add(allow(OperationMatch.action));

        AssignedOperationMatches accountGroup = new AssignedOperationMatches();
        accountGroup.add(deny(OperationMatch.action, "foo"));

        assertAllow(resolver.resolveAssignments(account,accountGroup),
                OperationMatch.action);

    }

    @Test
    public void testResolveAssignments_AccountGroupOverrideDeny()
    {
        AssignedOperationMatches account = new AssignedOperationMatches();
        account.add(allow(OperationMatch.service));

        AssignedOperationMatches accountGroup = new AssignedOperationMatches();
        accountGroup.add(deny(OperationMatch.action, "foo"));

        assertDeny(resolver.resolveAssignments(account, accountGroup),
                OperationMatch.action, "foo");

    }

    @Test
    public void testResolveAssignments_AccountGroupOverride()
    {
        AssignedOperationMatches account = new AssignedOperationMatches();
        account.add(deny(OperationMatch.service));

        AssignedOperationMatches accountGroup = new AssignedOperationMatches();
        accountGroup.add(allow(OperationMatch.action));

        assertAllow(resolver.resolveAssignments(account,accountGroup),
                OperationMatch.action);

    }

    @Test
    public void testResolveAssignments_AccountMerge()
    {
        AssignedOperationMatches account = new AssignedOperationMatches();
        account.add(allow(OperationMatch.service,"bar"));

        AssignedOperationMatches accountGroup = new AssignedOperationMatches();
        accountGroup.add(allow(OperationMatch.action, "foo"));

        assertAllow(resolver.resolveAssignments(account,accountGroup),
                OperationMatch.action,"foo","bar");

    }

    private OperationMatchResult allow(OperationMatch match,String... properties)
    {
        OperationMatchResult result = new OperationMatchResult();
        result.setAllowMatch(match);
        result.addAllowedProperties(Arrays.asList(properties));
        return result;
    }

    private OperationMatchResult deny(OperationMatch match,String... properties)
    {
        OperationMatchResult result = new OperationMatchResult();
        result.setDenyMatch(match);
        result.addDeniedProperties(Arrays.asList(properties));
        return result;
    }

    private void assertAllow(OperationMatchResult result,OperationMatch match,String... properties)
    {
        assertMatch(result,true,match, Util.asSet(properties));
    }

    private void assertDeny(OperationMatchResult result,OperationMatch match,String... properties)
    {
        assertMatch(result,false,match, Util.asSet(properties));
    }

    private void assertMatch(OperationMatchResult result,boolean allowed,OperationMatch match,Set<String> properties)
    {
        Assert.assertEquals(allowed,result.isAuthorized());
        if(allowed)
        {
            Assert.assertEquals(match, result.getAllowMatch());
            Assert.assertEquals(properties,result.getAllowedProperties());
        }
        else
        {
            Assert.assertEquals(match, result.getDenyMatch());
            Assert.assertEquals(properties,result.getDeniedProperties());
        }


    }
}
