package com.rubicon.platform.authorization.service.resolver;

import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.data.model.CompoundId;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;

public class SubjectAccountMatchResolverTest
{
    private static final CompoundId ACCOUNT_ONE = CompoundId.build("account/1");
    private static final CompoundId ACCOUNT_TWO = CompoundId.build("account/2");
    private static final CompoundId ACCOUNT_THREE = CompoundId.build("account/3");
    private static final CompoundId ACCOUNT_FOUR = CompoundId.build("account/4");
    private static final Long ACCOUNT_GROUP_ONE = 1L;
    private static final Long ACCOUNT_GROUP_TWO = 2L;

    private SubjectAccountMatchResolver resolver;
    private AccountResolver accountResolver;

    @Before
    public void setUp() throws Exception
    {
        resolver = new SubjectAccountMatchResolver();
        accountResolver = Mockito.mock(AccountResolver.class);
        resolver.setAccountResolver(accountResolver);

        Mockito.when(accountResolver.resolveAccountIds(ACCOUNT_GROUP_ONE)).thenReturn(Util.asList(ACCOUNT_ONE,ACCOUNT_TWO));
        Mockito.when(accountResolver.resolveAccountIds(ACCOUNT_GROUP_TWO)).thenReturn(Util.asList(ACCOUNT_TWO,ACCOUNT_THREE));
    }

    @Test
    public void testResolve_Context_Account_Authorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, allow(OperationMatch.service));

        assertAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts), ACCOUNT_ONE);
    }

    @Test
    public void testResolve_Context_Account_NotAuthorized() throws Exception
    {
        // test empty
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        assertNotAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts));


        subjectAccounts.addAccountMatch(ACCOUNT_ONE, deny(OperationMatch.action));
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, allow(OperationMatch.service));

        assertNotAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts));
    }

    @Test
    public void testResolve_Context_Group_Authorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.service));

        assertAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts), ACCOUNT_ONE);
    }

    @Test
    public void testResolve_Context_Group_NotAuthorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, deny(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.service));

        assertNotAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts));
    }

    @Test
    public void testResolve_Context_Mixed_Authorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.service));

        assertAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts), ACCOUNT_ONE);
    }

    @Test
    public void testResolve_Context_Mixed_NotAuthorized() throws Exception
    {

        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, deny(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.action));

        assertNotAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts));
    }

    @Test
    public void testResolve_Context_Mixed_Precedence() throws Exception
    {
        // equal allow and deny precedence - account group deny
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, deny(OperationMatch.action));

        assertAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts),ACCOUNT_ONE);

        // equal allow and deny precedence - account deny
        subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, deny(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.action));

        assertNotAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts));

        // account group allow precedence
        subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, deny(OperationMatch.resource));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.action));

        assertAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts), ACCOUNT_ONE);

        // account allow precedence
        subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.resource));

        assertAuthorized(resolver.resolve(ACCOUNT_ONE, subjectAccounts),ACCOUNT_ONE);
    }

    @Test
    public void testResolve_NoContext_Account_Authorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountMatch(ACCOUNT_TWO, allow(OperationMatch.service));

        assertAuthorized(resolver.resolve(null, subjectAccounts), ACCOUNT_ONE,ACCOUNT_TWO);

        // text mix
        subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, deny(OperationMatch.action));
        subjectAccounts.addAccountMatch(ACCOUNT_TWO, allow(OperationMatch.service));

        assertAuthorized(resolver.resolve(null, subjectAccounts),ACCOUNT_TWO);
    }

    @Test
    public void testResolve_NoContext_Account_NotAuthorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, deny(OperationMatch.action));
        subjectAccounts.addAccountMatch(ACCOUNT_TWO, deny(OperationMatch.service));

        assertNotAuthorized(resolver.resolve(null, subjectAccounts));
    }

    @Test
    public void testResolve_NoContext_Group_Authorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.action));

        assertAuthorized(resolver.resolve(null, subjectAccounts), ACCOUNT_ONE, ACCOUNT_TWO);
    }

    @Test
    public void testResolve_NoContext_Group_NotAuthorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, deny(OperationMatch.action));

        assertNotAuthorized(resolver.resolve(null, subjectAccounts));
    }

    @Test
    public void testResolve_NoContext_Group_Mixed() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_TWO, allow(OperationMatch.action));

        assertAuthorized(resolver.resolve(null, subjectAccounts), ACCOUNT_ONE, ACCOUNT_TWO, ACCOUNT_THREE);

        subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_ONE, deny(OperationMatch.action));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_TWO, allow(OperationMatch.action));

        assertAuthorized(resolver.resolve(null, subjectAccounts),ACCOUNT_THREE);
    }

    @Test
    public void testResolve_NoContext_Mixed_Authorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountMatch(ACCOUNT_FOUR, allow(OperationMatch.service));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_TWO, allow(OperationMatch.action));

        assertAuthorized(resolver.resolve(null, subjectAccounts), ACCOUNT_ONE, ACCOUNT_TWO,ACCOUNT_THREE,ACCOUNT_FOUR);
    }

    @Test
    public void testResolve_NoContext_Mixed_NotAuthorized() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_TWO, allow(OperationMatch.resource));
        subjectAccounts.addAccountMatch(ACCOUNT_FOUR, deny(OperationMatch.service));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_TWO, deny(OperationMatch.action));

        assertNotAuthorized(resolver.resolve(null, subjectAccounts));
    }


    @Test
    public void testResolve_NoContext_Mixed_Mixed() throws Exception
    {
        ResolvedSubjectAccounts subjectAccounts = new ResolvedSubjectAccounts();
        subjectAccounts.addAccountMatch(ACCOUNT_ONE, allow(OperationMatch.action));
        subjectAccounts.addAccountMatch(ACCOUNT_TWO, allow(OperationMatch.resource));
        subjectAccounts.addAccountMatch(ACCOUNT_FOUR, deny(OperationMatch.service));
        subjectAccounts.addAccountGroupMatch(ACCOUNT_GROUP_TWO, deny(OperationMatch.action));

        assertAuthorized(resolver.resolve(null, subjectAccounts),ACCOUNT_ONE);
    }




    private void assertAuthorized(SubjectAccountMatches result, CompoundId... account)
    {
        Assert.assertTrue(result.isAuthorized());
        Assert.assertEquals(new HashSet<>(Arrays.asList(account)),result.getAssignedAccounts());
    }

    private void assertNotAuthorized(SubjectAccountMatches result)
    {
        Assert.assertFalse(result.isAuthorized());
    }

    private OperationMatchResult allow(OperationMatch match)
    {
        OperationMatchResult result = new OperationMatchResult();
        result.setAllowMatch(match);
        return result;
    }


    private OperationMatchResult deny(OperationMatch match)
    {
        OperationMatchResult result = new OperationMatchResult();
        result.setDenyMatch(match);
        return result;
    }


}