package com.rubicon.platform.authorization.data.translator;

import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.Status;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 */
public class RoleAssignmentTranslatorTest extends BaseTranslatorFixture
{
    private RoleAssignmentTranslator translator;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        translator = new RoleAssignmentTranslator();
        translator.init();
    }

    @Test
    public void testConvertClient()
    {
        RoleAssignment roleAssignment = new RoleAssignment();
        roleAssignment.setAccountGroupId(1L);
        roleAssignment.setAccount("account/1");
        roleAssignment.setStatus(Status.DELETED); // should be ignored
        roleAssignment.setOwnerAccount("owner/1");
        roleAssignment.setRealm("realm");
        roleAssignment.setScope(Util.asList("1","2"));
        roleAssignment.setSubject("user/1");

        PersistentRoleAssignment persistentRoleAssignment = translator.convertClient(roleAssignment,context);
        Assert.assertNotNull(persistentRoleAssignment);
        Assert.assertEquals(new Long(1L), persistentRoleAssignment.getAccountGroupId());
        Assert.assertEquals(new CompoundId("account/1"), persistentRoleAssignment.getAccount());
        Assert.assertEquals(Status.ACTIVE, persistentRoleAssignment.getStatus());
        Assert.assertEquals(new CompoundId("owner/1"),persistentRoleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",persistentRoleAssignment.getRealm());
        Assert.assertEquals("[\"1\",\"2\"]",persistentRoleAssignment.getScope());
        Assert.assertEquals(new CompoundId("user/1"),persistentRoleAssignment.getSubject());
    }

    @Test
    public void testConvertClient_accountGroup()
    {
        RoleAssignment roleAssignment = new RoleAssignment();
        roleAssignment.setAccountGroupId(1L);
        roleAssignment.setOwnerAccount("owner/1");
        roleAssignment.setRealm("realm");
        roleAssignment.setScope(Util.asList("1","2"));
        roleAssignment.setSubject("user/1");

        PersistentRoleAssignment persistentRoleAssignment = translator.convertClient(roleAssignment,context);
        Assert.assertNotNull(persistentRoleAssignment);
        Assert.assertEquals(new Long(1), persistentRoleAssignment.getAccountGroupId());
        Assert.assertEquals(new CompoundId("",""), persistentRoleAssignment.getAccount());
        Assert.assertEquals(Status.ACTIVE, persistentRoleAssignment.getStatus());
        Assert.assertEquals(new CompoundId("owner/1"),persistentRoleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",persistentRoleAssignment.getRealm());
        Assert.assertEquals("[\"1\",\"2\"]",persistentRoleAssignment.getScope());
        Assert.assertEquals(new CompoundId("user/1"),persistentRoleAssignment.getSubject());
    }

    @Test
    public void testConvertClient_account()
    {
        RoleAssignment roleAssignment = new RoleAssignment();
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

        RoleAssignment roleAssignment = translator.convertPersistent(persistentRoleAssignment, context);
        Assert.assertNotNull(roleAssignment);
        Assert.assertEquals(new Long(1L), roleAssignment.getAccountGroupId());
        Assert.assertEquals("account/1", roleAssignment.getAccount());
        Assert.assertEquals(Status.DELETED, roleAssignment.getStatus());
        Assert.assertEquals("owner/1",roleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",roleAssignment.getRealm());
        Assert.assertEquals(Util.asList("1","2"),roleAssignment.getScope());
        Assert.assertEquals("user/1",roleAssignment.getSubject());
    }

    @Test
    public void testConvertPersistent_accountGroup()
    {
        PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
        persistentRoleAssignment.setAccountGroupId(1L);
        persistentRoleAssignment.setAccount(new CompoundId("",""));
        persistentRoleAssignment.setStatus(Status.DELETED);
        persistentRoleAssignment.setOwnerAccount(new CompoundId("owner/1"));
        persistentRoleAssignment.setRealm("realm");
        persistentRoleAssignment.setScope("[\"1\",\"2\"]");
        persistentRoleAssignment.setSubject(new CompoundId("user/1"));

        RoleAssignment roleAssignment = translator.convertPersistent(persistentRoleAssignment, context);
        Assert.assertNotNull(roleAssignment);
        Assert.assertEquals(new Long(1L), roleAssignment.getAccountGroupId());
        Assert.assertEquals(null, roleAssignment.getAccount());
        Assert.assertEquals(Status.DELETED, roleAssignment.getStatus());
        Assert.assertEquals("owner/1",roleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",roleAssignment.getRealm());
        Assert.assertEquals(Util.asList("1","2"),roleAssignment.getScope());
        Assert.assertEquals("user/1",roleAssignment.getSubject());
    }

    @Test
    public void testConvertPersistent_account()
    {
        PersistentRoleAssignment persistentRoleAssignment = new PersistentRoleAssignment();
        persistentRoleAssignment.setAccountGroupId(0L);
        persistentRoleAssignment.setAccount(new CompoundId("account/1"));
        persistentRoleAssignment.setStatus(Status.DELETED);
        persistentRoleAssignment.setOwnerAccount(new CompoundId("owner/1"));
        persistentRoleAssignment.setRealm("realm");
        persistentRoleAssignment.setScope("[\"1\",\"2\"]");
        persistentRoleAssignment.setSubject(new CompoundId("user/1"));

        RoleAssignment roleAssignment = translator.convertPersistent(persistentRoleAssignment, context);
        Assert.assertNotNull(roleAssignment);
        Assert.assertEquals(null, roleAssignment.getAccountGroupId());
        Assert.assertEquals("account/1", roleAssignment.getAccount());
        Assert.assertEquals(Status.DELETED, roleAssignment.getStatus());
        Assert.assertEquals("owner/1",roleAssignment.getOwnerAccount());
        Assert.assertEquals("realm",roleAssignment.getRealm());
        Assert.assertEquals(Util.asList("1","2"),roleAssignment.getScope());
        Assert.assertEquals("user/1",roleAssignment.getSubject());
    }
}
