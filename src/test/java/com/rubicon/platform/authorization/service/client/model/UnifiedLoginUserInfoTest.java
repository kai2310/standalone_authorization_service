package com.rubicon.platform.authorization.service.client.model;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class UnifiedLoginUserInfoTest extends TestAbstract
{
    @DataProvider
    public static Object[][] constructionDataProvider()
    {
        return new Object[][]{
                {"test", null, null},
                {"test", "seat", "seat"},
                {Constants.MAGNITE_STREAMING_PLATFORM, null, null},
                {Constants.MAGNITE_STREAMING_PLATFORM, "Seat", Constants.ACCOUNT_TYPE_STREAMING_SEAT},
                {Constants.MAGNITE_DV_PLUS, null, null},
                {Constants.MAGNITE_DV_PLUS, Constants.ACCOUNT_TYPE_BUYER, Constants.ACCOUNT_TYPE_SEAT},
                {Constants.MAGNITE_DV_PLUS, Constants.ACCOUNT_TYPE_PUBLISHER, Constants.ACCOUNT_TYPE_PUBLISHER},
                {Constants.MAGNITE_DV_PLUS, Constants.ACCOUNT_TYPE_MARKETPLACE_VENDOR,
                        Constants.ACCOUNT_TYPE_MARKETPLACE_VENDOR},

        };
    }

    @Test
    @UseDataProvider("constructionDataProvider")
    public void test(String platform, String contextType, String expected)
    {
        UnifiedLoginUserInfo unifiedLoginUserInfo =
                new UnifiedLoginUserInfo(platform, USER_EMAIL, contextType, STREAMING_SEAT_ID);

        assertEquals(expected, unifiedLoginUserInfo.getContextType());
    }
}
