package com.rubicon.platform.authorization.data.translator;

import com.rubicon.platform.authorization.data.model.PersistentRoleType;
import com.rubicon.platform.authorization.model.data.acm.RoleType;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 */
public class RoleTypeTranslatorTest extends BaseTranslatorFixture
{
    private RoleTypeTranslator  translator;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        translator = new RoleTypeTranslator();
        translator.init();
    }

    @Test
    public void testConvertClient()
    {
        RoleType roleType = new RoleType();
        roleType.setDescription("description");
        roleType.setLabel("label");

        PersistentRoleType persistentRoleType = translator.convertClient(roleType,context);
        Assert.assertNotNull(persistentRoleType);
        Assert.assertEquals("description", persistentRoleType.getDescription());
        Assert.assertEquals("label",persistentRoleType.getLabel());
    }

    @Test
    public void testConvertPersistent()
    {
        PersistentRoleType persistentRoleType = new PersistentRoleType();
        persistentRoleType.setDescription("description");
        persistentRoleType.setLabel("label");

        RoleType roleType = translator.convertPersistent(persistentRoleType,context);
        Assert.assertNotNull(roleType);
        Assert.assertEquals("description",roleType.getDescription());
        Assert.assertEquals("label",roleType.getLabel());
    }
}
