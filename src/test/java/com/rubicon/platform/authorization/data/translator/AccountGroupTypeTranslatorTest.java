package com.rubicon.platform.authorization.data.translator;

import com.rubicon.platform.authorization.data.model.PersistentAccountGroupType;
import com.rubicon.platform.authorization.model.data.acm.AccountGroupType;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 */
public class AccountGroupTypeTranslatorTest extends BaseTranslatorFixture
{
    private AccountGroupTypeTranslator translator;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        translator = new AccountGroupTypeTranslator();
        translator.init();
    }

    @Test
    public void testConvertClient()
    {
        AccountGroupType client = new AccountGroupType();
        client.setDescription("description");
        client.setLabel("label");

        PersistentAccountGroupType persistent = translator.convertClient(client,context);
        Assert.assertNotNull(persistent);
        Assert.assertEquals("description", persistent.getDescription());
        Assert.assertEquals("label",persistent.getLabel());
    }

    @Test
    public void testConvertPersistent()
    {
        PersistentAccountGroupType persistent = new PersistentAccountGroupType();
        persistent.setDescription("description");
        persistent.setLabel("label");

        AccountGroupType client = translator.convertPersistent(persistent,context);
        Assert.assertNotNull(client);
        Assert.assertEquals("description", client.getDescription());
        Assert.assertEquals("label",client.getLabel());
    }
}
