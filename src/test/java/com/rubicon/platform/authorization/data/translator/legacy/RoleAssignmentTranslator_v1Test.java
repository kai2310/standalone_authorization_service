package com.rubicon.platform.authorization.data.translator.legacy;

import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.data.api.legacy.RoleAssignment_v1;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentAccountGroup;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.translator.BaseRoleAssignmentTranslator;
import com.rubicon.platform.authorization.data.translator.BaseTranslatorFixture;
import com.rubicon.platform.authorization.data.translator.RoleAssignmentTranslator;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.Status;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

/**
 */
public class RoleAssignmentTranslator_v1Test extends BaseTranslatorFixture
{
    private RoleAssignmentTranslator_v1 translator;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        translator = new RoleAssignmentTranslator_v1();
        LegacyAccountFieldMapper accountFieldMapper = new LegacyAccountFieldMapper();
        accountFieldMapper.setAccountGroupMap(Collections.singletonMap("publisher",1L));
        translator.setAccountFieldMapper(accountFieldMapper);
        translator.init();
    }

    @Test
    public void testConvertClient()
    {
        RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
        roleAssignment.setAccount("account/1");
        roleAssignment.setOwnerAccount("owner/1");
        roleAssignment.setRealm("realm");
        roleAssignment.setScope(Util.asList("1","2"));
        roleAssignment.setSubject("user/1");

        PersistentRoleAssignment persistentRoleAssignment = translator.convertClient(roleAssignment,context);
        Assert.assertNotNull(persistentRoleAssignment);
        Assert.assertEquals(new Long(0), persistentRoleAssignment.getAccountGroupId());
        Assert.assertEquals(new CompoundId("account/1"), persistentRoleAssignment.getAccount());
        Assert.assertEquals(Status.ACTIVE, persistentRoleAssignment.getStatus());
        Assert.assertEquals(new CompoundId("owner/1"),persistentRoleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",persistentRoleAssignment.getRealm());
        Assert.assertEquals("[\"1\",\"2\"]",persistentRoleAssignment.getScope());
        Assert.assertEquals(new CompoundId("user/1"),persistentRoleAssignment.getSubject());
    }

    @Test
    public void testConvertClient_Wildcard()
    {
        RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
        roleAssignment.setAccount("publisher/*");
        roleAssignment.setOwnerAccount("owner/1");
        roleAssignment.setRealm("realm");
        roleAssignment.setScope(Util.asList("1","2"));
        roleAssignment.setSubject("user/1");

        PersistentRoleAssignment persistentRoleAssignment = translator.convertClient(roleAssignment,context);
        Assert.assertNotNull(persistentRoleAssignment);
        Assert.assertEquals(new Long(1), persistentRoleAssignment.getAccountGroupId());
        Assert.assertEquals(new CompoundId("", ""),persistentRoleAssignment.getAccount());
        Assert.assertEquals(Status.ACTIVE, persistentRoleAssignment.getStatus());
        Assert.assertEquals(new CompoundId("owner/1"),persistentRoleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",persistentRoleAssignment.getRealm());
        Assert.assertEquals("[\"1\",\"2\"]",persistentRoleAssignment.getScope());
        Assert.assertEquals(new CompoundId("user/1"),persistentRoleAssignment.getSubject());
    }

    @Test
    public void testCopy()
    {
        RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
        roleAssignment.setAccount("publisher/123");
        roleAssignment.setOwnerAccount("owner/1");
        roleAssignment.setRealm("realm");
        roleAssignment.setScope(Util.asList("1","2"));
        roleAssignment.setSubject("user/1");

        PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
        persistentRoleAssignment.setAccountGroupId(BaseRoleAssignmentTranslator.EMPTY_ACCOUNT_GROUP);
        persistentRoleAssignment.setAccount(new CompoundId("publisher/123"));
        persistentRoleAssignment.setStatus(Status.ACTIVE);
        persistentRoleAssignment.setOwnerAccount(new CompoundId("owner/1"));
        persistentRoleAssignment.setRealm("realm");
        persistentRoleAssignment.setScope("[\"1\",\"2\"]");
        persistentRoleAssignment.setSubject(new CompoundId("user/1"));

        translator.copyClient(roleAssignment, persistentRoleAssignment, context);

        Assert.assertNotNull(persistentRoleAssignment);
        Assert.assertEquals(BaseRoleAssignmentTranslator.EMPTY_ACCOUNT_GROUP, persistentRoleAssignment.getAccountGroupId());
        Assert.assertEquals(new CompoundId("publisher/123"),persistentRoleAssignment.getAccount());
        Assert.assertEquals(Status.ACTIVE, persistentRoleAssignment.getStatus());
        Assert.assertEquals(new CompoundId("owner/1"),persistentRoleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",persistentRoleAssignment.getRealm());
        Assert.assertEquals("[\"1\",\"2\"]",persistentRoleAssignment.getScope());
        Assert.assertEquals(new CompoundId("user/1"),persistentRoleAssignment.getSubject());

    }

    @Test
    public void testCopy_FlipAccountToAccountGroup()
    {
        RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
        roleAssignment.setAccount("publisher/*");
        roleAssignment.setOwnerAccount("owner/1");
        roleAssignment.setRealm("realm");
        roleAssignment.setScope(Util.asList("1","2"));
        roleAssignment.setSubject("user/1");

        PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
        persistentRoleAssignment.setAccountGroupId(BaseRoleAssignmentTranslator.EMPTY_ACCOUNT_GROUP);
        persistentRoleAssignment.setAccount(new CompoundId("publisher/*"));
        persistentRoleAssignment.setStatus(Status.DELETED);
        persistentRoleAssignment.setOwnerAccount(new CompoundId("owner/1"));
        persistentRoleAssignment.setRealm("realm");
        persistentRoleAssignment.setScope("[\"1\",\"2\"]");
        persistentRoleAssignment.setSubject(new CompoundId("user/1"));

        translator.copyClient(roleAssignment, persistentRoleAssignment, context);

        Assert.assertEquals(BaseRoleAssignmentTranslator.EMPTY_ACCOUNT,persistentRoleAssignment.getAccount());
        Assert.assertEquals(new Long(1L),persistentRoleAssignment.getAccountGroupId());

    }

    @Test
    public void testCopy_FlipAccountGroupToAccount()
    {
        RoleAssignment_v1 roleAssignment = new RoleAssignment_v1();
        roleAssignment.setAccount("publisher/100");
        roleAssignment.setOwnerAccount("owner/1");
        roleAssignment.setRealm("realm");
        roleAssignment.setScope(Util.asList("1","2"));
        roleAssignment.setSubject("user/1");

        PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
        persistentRoleAssignment.setAccountGroupId(1L);
        persistentRoleAssignment.setAccount(BaseRoleAssignmentTranslator.EMPTY_ACCOUNT);
        persistentRoleAssignment.setStatus(Status.DELETED);
        persistentRoleAssignment.setOwnerAccount(new CompoundId("owner/1"));
        persistentRoleAssignment.setRealm("realm");
        persistentRoleAssignment.setScope("[\"1\",\"2\"]");
        persistentRoleAssignment.setSubject(new CompoundId("user/1"));

        translator.copyClient(roleAssignment,persistentRoleAssignment,context);

        Assert.assertEquals(new CompoundId("publisher/100"),persistentRoleAssignment.getAccount());
        Assert.assertEquals(BaseRoleAssignmentTranslator.EMPTY_ACCOUNT_GROUP,persistentRoleAssignment.getAccountGroupId());

    }

    @Test
    public void testConvertPersistent()
    {
        PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
        persistentRoleAssignment.setAccountGroupId(1L);
        persistentRoleAssignment.setAccount(new CompoundId("account/1"));
        persistentRoleAssignment.setStatus(Status.DELETED);
        persistentRoleAssignment.setOwnerAccount(new CompoundId("owner/1"));
        persistentRoleAssignment.setRealm("realm");
        persistentRoleAssignment.setScope("[\"1\",\"2\"]");
        persistentRoleAssignment.setSubject(new CompoundId("user/1"));

        RoleAssignment_v1 roleAssignment = translator.convertPersistent(persistentRoleAssignment, context);
        Assert.assertNotNull(roleAssignment);
        Assert.assertEquals("account/1", roleAssignment.getAccount());
        Assert.assertEquals("owner/1",roleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",roleAssignment.getRealm());
        Assert.assertEquals(Util.asList("1","2"),roleAssignment.getScope());
        Assert.assertEquals("user/1",roleAssignment.getSubject());
    }

    @Test
    public void testConvertPersistent_AccountGroup()
    {
        PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
        persistentRoleAssignment.setAccountGroupId(1L);
        persistentRoleAssignment.setAccount(new CompoundId("", ""));
        persistentRoleAssignment.setStatus(Status.DELETED);
        persistentRoleAssignment.setOwnerAccount(new CompoundId("owner/1"));
        persistentRoleAssignment.setRealm("realm");
        persistentRoleAssignment.setScope("[\"1\",\"2\"]");
        persistentRoleAssignment.setSubject(new CompoundId("user/1"));

        PersistentAccountGroup group = new PersistentAccountGroup();
        group.setAccountType("publisher");
        persistentRoleAssignment.setAccountGroup(group);

        RoleAssignment_v1 roleAssignment = translator.convertPersistent(persistentRoleAssignment, context);
        Assert.assertNotNull(roleAssignment);
        Assert.assertEquals("publisher/*", roleAssignment.getAccount());
        Assert.assertEquals("owner/1",roleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",roleAssignment.getRealm());
        Assert.assertEquals(Util.asList("1","2"),roleAssignment.getScope());
        Assert.assertEquals("user/1",roleAssignment.getSubject());
    }
}
