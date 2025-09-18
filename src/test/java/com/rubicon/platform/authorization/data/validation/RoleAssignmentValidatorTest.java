package com.rubicon.platform.authorization.data.validation;

import com.dottydingo.hyperion.core.endpoint.HttpMethod;
import com.dottydingo.hyperion.core.validation.ValidationErrorContext;
import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.data.model.CompoundId;
import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.model.PersistentRoleAssignment;
import com.rubicon.platform.authorization.data.persistence.AccountGroupLoader;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.data.persistence.RoleAssignmentLoader;
import com.rubicon.platform.authorization.data.persistence.RoleLoader;
import com.rubicon.platform.authorization.model.data.acm.RoleAssignment;
import com.rubicon.platform.authorization.model.data.acm.Status;
import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 */
public class RoleAssignmentValidatorTest extends BaseHyperionValidatorFixture
{
    private final CompoundId subjectId = new CompoundId("user/1");
    private final CompoundId account = new CompoundId("account/1");
    private final CompoundId owner  = new CompoundId("publisher/100");

    private RoleAssignmentValidator validator;
    private AccountLoader accountLoader;
    private RoleLoader roleLoader;
    private RoleAssignmentLoader roleAssignmentLoader;
    private AccountGroupLoader accountGroupLoader;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        validator = new RoleAssignmentValidator();

        accountLoader = Mockito.mock(AccountLoader.class);
        validator.setAccountLoader(accountLoader);
        Mockito.when(accountLoader.exists(account)).thenReturn(true);
        Mockito.when(accountLoader.exists(owner)).thenReturn(true);

        roleLoader = Mockito.mock(RoleLoader.class);
        validator.setRoleLoader(roleLoader);

        PersistentRole persistentRole = new PersistentRole();
        persistentRole.setId(1L);
        persistentRole.setRealm("realm");
        Mockito.when(roleLoader.find(1L)).thenReturn(persistentRole);

        roleAssignmentLoader = Mockito.mock(RoleAssignmentLoader.class);
        validator.setRoleAssignmentLoader(roleAssignmentLoader);
        Mockito.when(roleAssignmentLoader.exists(subjectId, account,1L, null)).thenReturn(false);

        accountGroupLoader = Mockito.mock(AccountGroupLoader.class);
        validator.setAccountGroupLoader(accountGroupLoader);
        Mockito.when(accountGroupLoader.exists(10L)).thenReturn(true);

    }

    @Test
    public void testCreate_Success_account()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount(account.asIdString());
        client.setSubject(subjectId.asIdString());
        client.setOwnerAccount(owner.asIdString());
        client.setRoleId(1L);
        client.setScope(Util.asList("1","2"));

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testCreate_Success_accountGroup()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccountGroupId(10L);
        client.setSubject(subjectId.asIdString());
        client.setOwnerAccount(owner.asIdString());
        client.setRoleId(1L);
        client.setScope(Util.asList("1","2"));

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }


    @Test
    public void testCreate_accountExclusive()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccountGroupId(10L);
        client.setAccount(account.asIdString());
        client.setSubject(subjectId.asIdString());
        client.setOwnerAccount(owner.asIdString());
        client.setRoleId(1L);
        client.setScope(Util.asList("1","2"));

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());

        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.ACCOUNT_EXCLUSIVE, "RoleAssignment"));
    }

    @Test
    public void testCreate_ReadOnly()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccountGroupId(10L);
        client.setSubject(subjectId.asIdString());
        client.setOwnerAccount(owner.asIdString());
        client.setRoleId(1L);
        client.setScope(Util.asList("1","2"));
        client.setStatus(Status.ACTIVE);

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.READ_ONLY, "status"));
    }

    @Test
    public void testCreate_DuplicateScope()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount(account.asIdString());
        client.setSubject(subjectId.asIdString());
        client.setOwnerAccount(owner.asIdString());
        client.setRoleId(1L);
        client.setScope(Util.asList("1","1"));

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.DUPLICATE_ENTRIES, "scope"));
    }

    @Test
    public void testCreate_Required()
    {
        RoleAssignment client = new RoleAssignment();
        validator.validateCreate(client,errorContext,persistenceContext);

        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(4, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.REQUIRED_FIELD, "ownerAccount"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.REQUIRED_FIELD,"subject"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.ACCOUNT_REQUIRED,"RoleAssignment"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.REQUIRED_FIELD,"roleId"));
    }

    @Test
    public void testCreate_length()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount(account.asIdString());
        client.setSubject(subjectId.asIdString());
        client.setOwnerAccount(owner.asIdString());
        client.setRoleId(1L);

        client.setRealm(RandomStringUtils.randomAlphanumeric(65));

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(2, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.FIELD_LENGTH, "realm"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.REALM_MISMATCH, "realm"));
    }
    @Test
    public void testCreate_References()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount("account/2");
        client.setSubject(subjectId.asIdString());
        client.setOwnerAccount("account/3");
        client.setRoleId(2L);

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(3, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.NOT_FOUND, "account"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.NOT_FOUND, "ownerAccount"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.NOT_FOUND, "roleId"));

        errorContext = new ValidationErrorContext();
        client.setAccount(null);
        client.setAccountGroupId(21L);

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(3, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.NOT_FOUND, "accountGroupId"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.NOT_FOUND, "ownerAccount"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.NOT_FOUND, "roleId"));
    }


    @Test
    public void testCreate_InvalidAccounts()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount("x");
        client.setSubject("");
        client.setOwnerAccount("");
        client.setRoleId(1L);

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(3, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.INVALID_ID_FORMAT, "account"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.INVALID_ID_FORMAT, "subject"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.INVALID_ID_FORMAT, "ownerAccount"));

        client.setAccount("foo");
        client.setSubject("bar");
        client.setOwnerAccount("buzz");

        errorContext = new ValidationErrorContext();
        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(3, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.INVALID_ID_FORMAT, "account"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.INVALID_ID_FORMAT, "subject"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.INVALID_ID_FORMAT, "ownerAccount"));
    }

    @Test
    public void testCreate_InvalidAccounts_wildcards()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount("*/*");
        client.setSubject("*/*");
        client.setOwnerAccount("*/foo");
        client.setRoleId(1L);

        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(3, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(WildcardValidator.INVALID_ACCOUNT_TYPE_WILDCARD, "ownerAccount"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.SUBJECT_WILDCARD_NOT_ALLOWED, "subject"));
        Assert.assertTrue(errorContext.containsError(WildcardValidator.WILDCARD_NOT_ALLOWED, "account"));

        client.setAccount("publisher/*");
        client.setSubject("*/foo");
        client.setOwnerAccount("foo/*");

        errorContext = new ValidationErrorContext();
        validator.validateCreate(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(2, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.SUBJECT_WILDCARD_NOT_ALLOWED, "subject"));
        Assert.assertTrue(errorContext.containsError(WildcardValidator.WILDCARD_NOT_ALLOWED, "account"));
    }

    @Test
    public void testCreate_Duplicate_account()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount("account/1");
        client.setSubject("user/1");
        client.setRoleId(1L);

        Mockito.when(roleAssignmentLoader.exists(new CompoundId("user/1"),new CompoundId("account/1"),1L,null)).thenReturn(true);

        validator.validateCreateConflict(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.DUPLICATE_ROLE_ASSIGNMENT,"RoleAssignment"));
    }

    @Test
    public void testCreate_Duplicate_accountGroup()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccountGroupId(2L);
        client.setSubject("user/1");
        client.setRoleId(1L);

        Mockito.when(roleAssignmentLoader.exists(new CompoundId("user/1"),null,1L,2L)).thenReturn(true);

        validator.validateCreateConflict(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.DUPLICATE_ROLE_ASSIGNMENT,"RoleAssignment"));
    }

    @Test
    public void testUpdate_Success()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount(account.asIdString());
        client.setOwnerAccount(owner.asIdString());
        client.setSubject(subjectId.asIdString());
        client.setRoleId(1L);
        client.setScope(Util.asList("1","2"));
        client.setRealm("realm");
        client.setStatus(Status.ACTIVE);

        PersistentRoleAssignment persistent = new PersistentRoleAssignment();
        persistent.setAccount(account);
        persistent.setSubject(subjectId);
        persistent.setRoleId(1L);
        persistent.setRealm("realm");
        persistent.setStatus(Status.ACTIVE);

        validator.validateUpdate(client,persistent,errorContext,persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }


    @Test
    public void testUpdate_change()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount("account/2");
        client.setSubject("user/2");
        client.setRoleId(2L);
        client.setScope(Util.asList("1","2"));
        client.setRealm("realm2");
        client.setStatus(Status.DELETED);
        client.setAccountGroupId(1L);

        PersistentRoleAssignment persistent = new PersistentRoleAssignment();
        persistent.setAccount(account);
        persistent.setSubject(subjectId);
        persistent.setRoleId(1L);
        persistent.setRealm("realm");
        persistent.setStatus(Status.ACTIVE);
        persistent.setAccountGroupId(2L);

        validator.validateUpdate(client,persistent,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(6, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.CHANGE_NOT_ALLOWED, "subject"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.CHANGE_NOT_ALLOWED,"account"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.CHANGE_NOT_ALLOWED,"roleId"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.CHANGE_NOT_ALLOWED,"realm"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.CHANGE_NOT_ALLOWED,"status"));
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.CHANGE_NOT_ALLOWED,"accountGroupId"));
    }

    @Test
    public void testUpdate_OwnerAccount()
    {
        RoleAssignment client = new RoleAssignment();
        client.setOwnerAccount("");

        PersistentRoleAssignment persistent = new PersistentRoleAssignment();

        validator.validateUpdate(client, persistent, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.INVALID_ID_FORMAT, "ownerAccount"));

        client.setOwnerAccount("buzz");

        errorContext = new ValidationErrorContext();
        validator.validateUpdate(client, persistent, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.INVALID_ID_FORMAT, "ownerAccount"));
    }

    @Test
    public void testUpdate_OwnerAccountWildcards()
    {
        RoleAssignment client = new RoleAssignment();
        client.setOwnerAccount("*/foo");

        PersistentRoleAssignment persistent = new PersistentRoleAssignment();

        validator.validateUpdate(client, persistent, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(WildcardValidator.INVALID_ACCOUNT_TYPE_WILDCARD, "ownerAccount"));

    }

    @Test
    public void testUpdate_DuplicateScope()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount(account.asIdString());
        client.setSubject(subjectId.asIdString());
        client.setRoleId(1L);
        client.setScope(Util.asList("1","1"));
        client.setRealm("realm");

        PersistentRoleAssignment persistent = new PersistentRoleAssignment();
        persistent.setAccount(account);
        persistent.setSubject(subjectId);
        persistent.setRoleId(1L);
        persistent.setRealm("realm");

        validator.validateUpdate(client, persistent, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.DUPLICATE_ENTRIES, "scope"));
    }

    @Test
    public void testUpdate_Deleted()
    {
        RoleAssignment client = new RoleAssignment();
        client.setAccount(account.asIdString());
        client.setSubject(subjectId.asIdString());
        client.setRoleId(1L);
        client.setScope(Util.asList("1","1"));
        client.setRealm("realm");

        PersistentRoleAssignment persistent = new PersistentRoleAssignment();
        persistent.setAccount(account);
        persistent.setSubject(subjectId);
        persistent.setRoleId(1L);
        persistent.setRealm("realm");
        persistent.setStatus(Status.DELETED);

        persistenceContext.setHttpMethod(HttpMethod.PUT);

        validator.validateUpdate(client,persistent,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleAssignmentValidator.DELETED_ITEM, "RoleAssignment"));
    }


}
