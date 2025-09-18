package com.rubicon.platform.authorization.data.validation;

import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.data.model.PersistentAccountGroup;
import com.rubicon.platform.authorization.data.persistence.AccountGroupLoader;
import com.rubicon.platform.authorization.data.persistence.AccountGroupTypeLoader;
import com.rubicon.platform.authorization.data.persistence.AccountLoader;
import com.rubicon.platform.authorization.model.data.acm.AccountGroup;
import com.rubicon.platform.authorization.model.data.acm.Status;
import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;

/**
 */
public class AccountGroupValidatorTest extends BaseHyperionValidatorFixture
{
    private AccountGroupValidator validator;
    private AccountGroupLoader accountGroupLoader;
    private AccountGroupTypeLoader accountGroupTypeLoader;
    private AccountLoader accountLoader;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        validator = new AccountGroupValidator();
        accountGroupLoader = Mockito.mock(AccountGroupLoader.class);
        validator.setAccountGroupLoader(accountGroupLoader);

        accountGroupTypeLoader = Mockito.mock(AccountGroupTypeLoader.class);
        validator.setAccountGroupTypeLoader(accountGroupTypeLoader);
        Mockito.when(accountGroupTypeLoader.exists(1L)).thenReturn(true);

        accountLoader = Mockito.mock(AccountLoader.class);
        validator.setAccountLoader(accountLoader);
        Mockito.when(accountLoader.findIds(Collections.singleton(10L))).thenReturn(Collections.singletonList(10L));
    }

    @Test
    public void testCreate_Success()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel(RandomStringUtils.randomAlphanumeric(64));
        accountGroup.setDescription("Bar");
        accountGroup.setAccountGroupTypeId(1L);
        accountGroup.setAccountIds(Collections.singletonList(10L));

        validator.validateCreate(accountGroup,errorContext,persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testCreate_ReadOnly()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel(RandomStringUtils.randomAlphanumeric(64));
        accountGroup.setDescription("Bar");
        accountGroup.setAccountGroupTypeId(1L);
        accountGroup.setAccountIds(Collections.singletonList(10L));
        accountGroup.setStatus(Status.ACTIVE);

        validator.validateCreate(accountGroup,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.READ_ONLY, "status"));
    }

    @Test
    public void testCreate_Required()
    {
        AccountGroup accountGroup = new AccountGroup();

        validator.validateCreate(accountGroup,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(3, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.REQUIRED_FIELD, "label"));
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.REQUIRED_FIELD,"accountGroupTypeId"));
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.ACCOUNT_GROUP_ACCOUNT_REQUIRED,"AccountGroup"));
    }

    @Test
    public void testCreate_Blank()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel("");
        accountGroup.setAccountGroupTypeId(1L);
        accountGroup.setAccountIds(new ArrayList<Long>());

        validator.validateCreate(accountGroup, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(2, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.REQUIRED_FIELD, "label"));
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.EMPTY,"accountIds"));
    }

    @Test
    public void testCreate_AccountIdsAndAccountType()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel(RandomStringUtils.randomAlphanumeric(64));
        accountGroup.setDescription("Bar");
        accountGroup.setAccountGroupTypeId(1L);
        accountGroup.setAccountIds(Collections.singletonList(10L));
        accountGroup.setAccountType("foo");

        validator.validateCreate(accountGroup, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.ACCOUNT_GROUP_ACCOUNT_EXCLUSIVE, "AccountGroup"));
    }

    @Test
    public void testCreate_NullEntries()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel("label");
        accountGroup.setAccountGroupTypeId(1L);
        accountGroup.setAccountIds(Util.asList(10L,null));

        validator.validateCreate(accountGroup, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.COLLECTION_NULL_VALUES,"accountIds"));
    }

    @Test
    public void testCreate_length()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel(RandomStringUtils.randomAlphanumeric(65));
        accountGroup.setAccountGroupTypeId(1L);
        accountGroup.setAccountType(RandomStringUtils.randomAlphabetic(65));

        validator.validateCreate(accountGroup, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(2,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.FIELD_LENGTH,"label"));
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.FIELD_LENGTH,"accountType"));
    }

    @Test
    public void testCreate_References()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel(RandomStringUtils.randomAlphanumeric(64));
        accountGroup.setAccountGroupTypeId(2L);
        accountGroup.setAccountIds(Collections.singletonList(11L));

        validator.validateCreate(accountGroup, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(2, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.NOT_FOUND, "accountGroupTypeId"));
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.NOT_FOUND_IDS,"accountIds"));
    }

    @Test
    public void testCreate_unique()
    {
        Mockito.when(accountGroupLoader.isLabelUnique("test")).thenReturn(false);
        AccountGroup client = new AccountGroup();
        client.setAccountGroupTypeId(1L);
        client.setLabel("test");

        validator.validateCreateConflict(client, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.NOT_UNIQUE,"label"));
    }

    @Test
    public void testUpdate_Success()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel(RandomStringUtils.randomAlphanumeric(64));
        accountGroup.setDescription("Bar");
        accountGroup.setAccountGroupTypeId(2L);
        accountGroup.setAccountIds(Collections.singletonList(11L));

        PersistentAccountGroup persistentAccountGroup = new PersistentAccountGroup();
        persistentAccountGroup.setAccountGroupTypeId(2L);
        persistentAccountGroup.setAccountIds(Collections.singletonList(11L));

        validator.validateUpdate(accountGroup, persistentAccountGroup, errorContext, persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testUpdate_Blank()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel("");
        accountGroup.setAccountIds(new ArrayList<Long>());

        PersistentAccountGroup persistentAccountGroup = new PersistentAccountGroup();

        validator.validateUpdate(accountGroup,persistentAccountGroup,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(2, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.REQUIRED_FIELD, "label"));
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.EMPTY, "accountIds"));
    }

    @Test
    public void testUpdate_Length()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel(RandomStringUtils.randomAlphanumeric(65));
        accountGroup.setAccountType(RandomStringUtils.randomAlphanumeric(65));

        PersistentAccountGroup persistentAccountGroup = new PersistentAccountGroup();
        persistentAccountGroup.setAccountType("foo");

        validator.validateUpdate(accountGroup,persistentAccountGroup,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(2, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.FIELD_LENGTH, "label"));
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.FIELD_LENGTH, "accountType"));
    }

    @Test
    public void testUpdate_FromDynamic()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setAccountIds(Util.asList(10L));

        PersistentAccountGroup persistentAccountGroup = new PersistentAccountGroup();
        persistentAccountGroup.setAccountType("foo");

        validator.validateUpdate(accountGroup,persistentAccountGroup,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.DYNAMIC_ACCOUNT_GROUP_TO_ACCOUNTS, "AccountGroup"));
    }

    @Test
    public void testUpdate_ToDynamic()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setAccountType("foo");

        PersistentAccountGroup persistentAccountGroup = new PersistentAccountGroup();
        persistentAccountGroup.setAccountIds(Util.asList(10L));

        validator.validateUpdate(accountGroup,persistentAccountGroup,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.ACCOUNTS_TO_DYNAMIC_ACCOUNT_GROUP, "AccountGroup"));
    }

    @Test
    public void testUpdate_NullEntries()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setAccountIds(Util.asList(10L,null));

        PersistentAccountGroup persistentAccountGroup = new PersistentAccountGroup();

        validator.validateUpdate(accountGroup,persistentAccountGroup,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.COLLECTION_NULL_VALUES, "accountIds"));
    }

    @Test
    public void testUpdate_References()
    {
        AccountGroup accountGroup = new AccountGroup();
        accountGroup.setLabel(RandomStringUtils.randomAlphanumeric(64));
        accountGroup.setAccountGroupTypeId(2L);
        accountGroup.setAccountIds(Collections.singletonList(11L));

        PersistentAccountGroup persistentAccountGroup = new PersistentAccountGroup();
        persistentAccountGroup.setAccountGroupTypeId(1L);
        persistentAccountGroup.setAccountIds(Collections.singletonList(10L));

        validator.validateUpdate(accountGroup,persistentAccountGroup,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(2, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.NOT_FOUND, "accountGroupTypeId"));
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.NOT_FOUND_IDS, "accountIds"));
    }

    @Test
    public void testUpdate_unique()
    {
        Mockito.when(accountGroupLoader.isLabelUnique("test1",1L)).thenReturn(false);

        AccountGroup client = new AccountGroup();
        client.setLabel("test1");

        PersistentAccountGroup persistent = new PersistentAccountGroup();
        persistent.setId(1L);
        persistent.setLabel("test");

        validator.validateUpdateConflict(client,persistent,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.NOT_UNIQUE, "label"));
    }

    @Test
    public void testDelete_Success()
    {

        PersistentAccountGroup persistentAccountGroup = new PersistentAccountGroup();
        persistentAccountGroup.setId(1L);

        Mockito.when(accountGroupLoader.hasReferences(1L)).thenReturn(false);

        validator.validateDeleteConflict(persistentAccountGroup, errorContext, persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testDelete_Conflict()
    {
        PersistentAccountGroup persistentAccountGroup = new PersistentAccountGroup();
        persistentAccountGroup.setId(1L);

        Mockito.when(accountGroupLoader.hasReferences(1L)).thenReturn(true);

        validator.validateDeleteConflict(persistentAccountGroup,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupValidator.DELETE_CONFLICT, "id"));
    }
}
