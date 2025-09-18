package com.rubicon.platform.authorization.data.validation;

import com.rubicon.platform.authorization.data.model.PersistentAccountGroupType;
import com.rubicon.platform.authorization.data.persistence.AccountGroupTypeLoader;
import com.rubicon.platform.authorization.model.data.acm.AccountGroupType;
import com.rubicon.platform.authorization.model.data.acm.Status;
import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 */
public class AccountGroupTypeValidatorTest extends BaseHyperionValidatorFixture
{
    private AccountGroupTypeValidator validator;
    private AccountGroupTypeLoader accountGroupTypeLoader;


    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        validator = new AccountGroupTypeValidator();
        accountGroupTypeLoader = Mockito.mock(AccountGroupTypeLoader.class);
        validator.setAccountGroupTypeLoader(accountGroupTypeLoader);
    }

    @Test
    public void testCreate_Success()
    {
        AccountGroupType accountGroupType = new AccountGroupType();
        accountGroupType.setLabel(RandomStringUtils.randomAlphanumeric(64));
        accountGroupType.setDescription("Bar");

        validator.validateCreate(accountGroupType,errorContext,persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testCreate_ReadOnly()
    {
        AccountGroupType accountGroupType = new AccountGroupType();
        accountGroupType.setLabel(RandomStringUtils.randomAlphanumeric(64));
        accountGroupType.setDescription("Bar");
        accountGroupType.setStatus(Status.DELETED);

        validator.validateCreate(accountGroupType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.READ_ONLY,"status"));
    }

    @Test
    public void testCreate_Required()
    {
        AccountGroupType accountGroupType = new AccountGroupType();

        validator.validateCreate(accountGroupType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.REQUIRED_FIELD,"label"));
    }

    @Test
    public void testCreate_Blank()
    {
        AccountGroupType accountGroupType = new AccountGroupType();
        accountGroupType.setLabel("");

        validator.validateCreate(accountGroupType, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.REQUIRED_FIELD,"label"));
    }

    @Test
    public void testCreate_length()
    {
        AccountGroupType accountGroupType = new AccountGroupType();
        accountGroupType.setLabel(RandomStringUtils.randomAlphanumeric(65));

        validator.validateCreate(accountGroupType, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.FIELD_LENGTH,"label"));
    }

    @Test
    public void testCreate_unique()
    {
        Mockito.when(accountGroupTypeLoader.isLabelUnique("test")).thenReturn(false);
        AccountGroupType accountGroupType = new AccountGroupType();
        accountGroupType.setLabel("test");

        validator.validateCreateConflict(accountGroupType, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.NOT_UNIQUE,"label"));
    }

    @Test
    public void testUpdate_Success()
    {
        AccountGroupType accountGroupType = new AccountGroupType();
        accountGroupType.setLabel(RandomStringUtils.randomAlphanumeric(64));
        accountGroupType.setDescription("Bar");

        PersistentAccountGroupType persistentAccountGroupType = new PersistentAccountGroupType();

        validator.validateUpdate(accountGroupType, persistentAccountGroupType, errorContext, persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testUpdate_Blank()
    {
        AccountGroupType accountGroupType = new AccountGroupType();
        accountGroupType.setLabel("");

        PersistentAccountGroupType persistentAccountGroupType = new PersistentAccountGroupType();

        validator.validateUpdate(accountGroupType,persistentAccountGroupType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.REQUIRED_FIELD, "label"));
    }

    @Test
    public void testUpdate_Length()
    {
        AccountGroupType accountGroupType = new AccountGroupType();
        accountGroupType.setLabel(RandomStringUtils.randomAlphanumeric(65));

        PersistentAccountGroupType persistentAccountGroupType = new PersistentAccountGroupType();

        validator.validateUpdate(accountGroupType,persistentAccountGroupType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.FIELD_LENGTH, "label"));
    }

    @Test
    public void testUpdate_unique()
    {
        Mockito.when(accountGroupTypeLoader.isLabelUnique("test1",1L)).thenReturn(false);

        AccountGroupType accountGroupType = new AccountGroupType();
        accountGroupType.setLabel("test1");

        PersistentAccountGroupType persistentAccountGroupType = new PersistentAccountGroupType();
        persistentAccountGroupType.setId(1L);
        persistentAccountGroupType.setLabel("test");

        validator.validateUpdateConflict(accountGroupType,persistentAccountGroupType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.NOT_UNIQUE, "label"));
    }

    @Test
    public void testDelete_Success()
    {

        PersistentAccountGroupType persistentAccountGroupType = new PersistentAccountGroupType();
        persistentAccountGroupType.setId(1L);

        Mockito.when(accountGroupTypeLoader.hasReferences(1L)).thenReturn(false);

        validator.validateDeleteConflict(persistentAccountGroupType, errorContext, persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testDelete_Conflict()
    {

        PersistentAccountGroupType persistentAccountGroupType = new PersistentAccountGroupType();
        persistentAccountGroupType.setId(1L);

        Mockito.when(accountGroupTypeLoader.hasReferences(1L)).thenReturn(true);

        validator.validateDeleteConflict(persistentAccountGroupType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(AccountGroupTypeValidator.DELETE_CONFLICT, "id"));
    }
}
