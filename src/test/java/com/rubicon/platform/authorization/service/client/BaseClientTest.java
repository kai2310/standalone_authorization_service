package com.rubicon.platform.authorization.service.client;

import com.rubicon.platform.authorization.service.utils.Constants;
import com.rubicon.platform.authorization.service.client.BaseClient;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(DataProviderRunner.class)
public class BaseClientTest
{
    private BaseClient baseClient = new BaseClient("baseUrl");

    @DataProvider
    public static Object[][] appendQueryParamsDataProvider()
    {
        String key = "key";
        String value = "value";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(key, value);

        return new Object[][]{
                {null, ""},
                {new HashMap<>(), ""},
                {queryParams, "?".concat(key).concat("=").concat(value)}
        };
    }

    @Test
    @UseDataProvider("appendQueryParamsDataProvider")
    public void appendQueryParamsTest(Map<String, Object> queryParams, String expected)
    {
        Assert.assertEquals(expected, baseClient.appendQueryParams(queryParams));
    }

    @Test
    public void appendAccessTokenQueryParamsTest()
    {
        String token = "123";
        String expected = "?".concat(Constants.QUERY_PARAMETER_ACCESS_TOKEN).concat("=123");

        Assert.assertEquals(expected, baseClient.appendAccessTokenQueryParams(token));
    }

    @DataProvider
    public static Object[][] appendQueryFilterParamsDataProvider()
    {
        String key = "key";
        String value = "value";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put(key, value);

        return new Object[][]{
                {null, ""},
                {new HashMap<>(), ""},
                {queryParams, "query=".concat(key).concat("==").concat(value)}
        };
    }

    @Test
    @UseDataProvider("appendQueryFilterParamsDataProvider")
    public void appendQueryFilterParamsTest(Map<String, Object> filterMap, String expected)
    {
        Assert.assertEquals(expected, baseClient.appendQueryFilterParams(filterMap));
    }
}
