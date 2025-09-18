package com.rubicon.platform.authorization.service.jobs;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.translator.TranslationContext;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(DataProviderRunner.class)
public class DataMarketplaceVendorResponseConverterTest extends TestAbstract
{
    private DataMarketplaceVendorResponseConverter converter = new DataMarketplaceVendorResponseConverter();
    private TranslationContext context = new TranslationContext();

    @DataProvider
    public static Object[][] convertToPersistentValueDataProvider()
    {
        return new Object[][]{
                {false},
                {true},
        };
    }

    @Test
    @UseDataProvider("convertToPersistentValueDataProvider")
    public void convertToPersistentValueTest(boolean hasMarketplaceVendors)
    {
        List<DataMarketplaceVendor> marketplaceVendorList = hasMarketplaceVendors
                                                            ? Arrays.asList(getDataMarketplaceVendor())
                                                            : null;

        RevvAccountResponse revvAccounts = converter.convertToPersistentValue(marketplaceVendorList, context);

        if (hasMarketplaceVendors)
        {
            Assert.assertEquals(1, revvAccounts.size());
            RevvAccount revvAccount = revvAccounts.get(0);
            Assert.assertEquals(String.valueOf(DATA_MARKETPLACE_VENDOR_ID), revvAccount.getId());
            Assert.assertEquals(DATA_MARKETPLACE_VENDOR, revvAccount.getLabel());
            Assert.assertEquals(DATA_MARKETPLACE_STATUS, revvAccount.getStatus());
        }
        else
        {
            Assert.assertEquals(0, revvAccounts.size());
        }
    }

    @Test
    public void convertToClientValueTest()
    {
        Assert.assertNull(converter.convertToClientValue(new RevvAccountResponse(), context));
    }
}
