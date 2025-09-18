package com.rubicon.platform.authorization.data.translator;

import com.rubicon.platform.authorization.Util;
import com.rubicon.platform.authorization.data.model.PersistentAccountGroup;
import com.rubicon.platform.authorization.model.data.acm.AccountGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 */
public class AccountGroupTranslatorTest extends BaseTranslatorFixture
{
    private AccountGroupTranslator translator;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        translator = new AccountGroupTranslator();
        translator.init();
    }

    @Test
    public void testConvertClient()
    {

        AccountGroup client = new AccountGroup();
        client.setDescription("description");
        client.setLabel("label");
        client.setAccountGroupTypeId(1L);
        client.setAccountIds(Util.asList(10L, 11L,11L, 12L));

        PersistentAccountGroup persistent = translator.convertClient(client,context);
        Assert.assertNotNull(persistent);
        Assert.assertEquals("description", persistent.getDescription());
        Assert.assertEquals("label",persistent.getLabel());
        Assert.assertEquals(new Long(1L),persistent.getAccountGroupTypeId());
        Assert.assertEquals(Util.asList(10L, 11L, 12L),persistent.getAccountIds());
    }

    @Test
    public void testConvertPersistent()
    {
        PersistentAccountGroup persistent = new PersistentAccountGroup();
        persistent.setDescription("description");
        persistent.setLabel("label");
        persistent.setAccountGroupTypeId(1L);
        persistent.setAccountIds(Util.asList(10L, 11L, 12L));

        AccountGroup client = translator.convertPersistent(persistent,context);
        Assert.assertNotNull(client);
        Assert.assertEquals("description", client.getDescription());
        Assert.assertEquals("label",client.getLabel());
        Assert.assertEquals(1, client.getAccountGroupTypeId().longValue());
        Assert.assertEquals(Util.asList(10L, 11L, 12L),client.getAccountIds());
    }

    @Test
    public void testCopy()
    {

        AccountGroup client = new AccountGroup();
        client.setDescription("description2");
        client.setLabel("label2");
        client.setAccountGroupTypeId(2L);
        client.setAccountIds(Util.asList(10L, 11L,11L));

        PersistentAccountGroup persistent = new PersistentAccountGroup();
        persistent.setDescription("description");
        persistent.setLabel("label");
        persistent.setAccountGroupTypeId(1L);
        persistent.setAccountIds(Util.asList(10L, 11L, 12L));

        translator.copyClient(client,persistent,context);
        Assert.assertEquals("description2", persistent.getDescription());
        Assert.assertEquals("label2",persistent.getLabel());
        Assert.assertEquals(2, persistent.getAccountGroupTypeId().longValue());
        Assert.assertEquals(Util.asList(10L, 11L),persistent.getAccountIds());
    }
}
