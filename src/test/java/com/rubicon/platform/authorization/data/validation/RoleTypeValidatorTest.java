package com.rubicon.platform.authorization.data.validation;

import com.rubicon.platform.authorization.data.model.PersistentRole;
import com.rubicon.platform.authorization.data.model.PersistentRoleType;
import com.rubicon.platform.authorization.data.persistence.RoleTypeLoader;
import com.rubicon.platform.authorization.model.data.acm.RoleType;
import com.rubicon.platform.authorization.model.data.acm.Status;
import junit.framework.Assert;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 */
public class RoleTypeValidatorTest extends BaseHyperionValidatorFixture
{
    private RoleTypeValidator validator;
    private RoleTypeLoader roleTypeLoader;


    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        validator = new RoleTypeValidator();
        roleTypeLoader = Mockito.mock(RoleTypeLoader.class);
        validator.setRoleTypeLoader(roleTypeLoader);
    }

    @Test
    public void testCreate_Success()
    {
        RoleType roleType = new RoleType();
        roleType.setLabel(RandomStringUtils.randomAlphanumeric(64));
        roleType.setDescription("Bar");

        validator.validateCreate(roleType,errorContext,persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testCreate_ReadOnly()
    {
        RoleType roleType = new RoleType();
        roleType.setLabel(RandomStringUtils.randomAlphanumeric(64));
        roleType.setDescription("Bar");
        roleType.setStatus(Status.ACTIVE);

        validator.validateCreate(roleType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleTypeValidator.READ_ONLY, "status"));
    }

    @Test
    public void testCreate_Required()
    {
        RoleType roleType = new RoleType();

        validator.validateCreate(roleType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleTypeValidator.REQUIRED_FIELD,"label"));
    }

    @Test
    public void testCreate_Blank()
    {
        RoleType roleType = new RoleType();
        roleType.setLabel("");

        validator.validateCreate(roleType, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleTypeValidator.REQUIRED_FIELD,"label"));
    }

    @Test
    public void testCreate_length()
    {
        RoleType roleType = new RoleType();
        roleType.setLabel(RandomStringUtils.randomAlphanumeric(65));

        validator.validateCreate(roleType, errorContext, persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleTypeValidator.FIELD_LENGTH,"label"));
    }

    @Test
    public void testCreate_Conflict()
    {
        RoleType client = new RoleType();
        client.setLabel("duplicate");

        Mockito.when(roleTypeLoader.isLabelUnique("duplicate")).thenReturn(false);
        validator.validateCreateConflict(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleTypeValidator.NOT_UNIQUE,"label"));
    }

    @Test
    public void testUpdate_Success()
    {
        RoleType roleType = new RoleType();
        roleType.setLabel(RandomStringUtils.randomAlphanumeric(64));
        roleType.setDescription("Bar");

        PersistentRoleType persistentRoleType = new PersistentRoleType();

        validator.validateUpdate(roleType, persistentRoleType, errorContext, persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testUpdate_Blank()
    {
        RoleType roleType = new RoleType();
        roleType.setLabel("");

        PersistentRoleType persistentRoleType = new PersistentRoleType();

        validator.validateUpdate(roleType,persistentRoleType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleTypeValidator.REQUIRED_FIELD, "label"));
    }

    @Test
    public void testUpdate_Length()
    {
        RoleType roleType = new RoleType();
        roleType.setLabel(RandomStringUtils.randomAlphanumeric(65));

        PersistentRoleType persistentRoleType = new PersistentRoleType();

        validator.validateUpdate(roleType,persistentRoleType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleTypeValidator.FIELD_LENGTH, "label"));
    }

    @Test
    public void testUpdate_Conflict()
    {
        RoleType client = new RoleType();
        client.setLabel("duplicate");

        PersistentRoleType persistent = new PersistentRoleType();
        persistent.setId(1L);
        persistent.setLabel("something");

        Mockito.when(roleTypeLoader.isLabelUnique("duplicate",1L)).thenReturn(false);

        validator.validateCreateConflict(client,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1,errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleTypeValidator.NOT_UNIQUE,"label"));
    }

    @Test
    public void testDelete_Success()
    {

        PersistentRoleType persistentRoleType = new PersistentRoleType();
        persistentRoleType.setId(1L);

        Mockito.when(roleTypeLoader.hasReferences(1L)).thenReturn(false);

        validator.validateDeleteConflict(persistentRoleType, errorContext, persistenceContext);
        Assert.assertFalse(errorContext.hasErrors());
    }

    @Test
    public void testDelete_Conflict()
    {

        PersistentRoleType persistentRoleType = new PersistentRoleType();
        persistentRoleType.setId(1L);

        Mockito.when(roleTypeLoader.hasReferences(1L)).thenReturn(true);

        validator.validateDeleteConflict(persistentRoleType,errorContext,persistenceContext);
        Assert.assertTrue(errorContext.hasErrors());
        Assert.assertEquals(1, errorContext.getValidationErrors().size());
        Assert.assertTrue(errorContext.containsError(RoleTypeValidator.DELETE_CONFLICT, "id"));
    }
}
