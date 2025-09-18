package com.rubicon.platform.authorization.service.v1.ui.resolver.endpoint;

import com.rubicon.platform.authorization.service.exception.BadRequestException;
import com.rubicon.platform.authorization.service.v1.ui.resolver.AccountServiceResolver;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecification;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AccountListRequestAdapterTest extends AbstractListRequestAdapterTest
{
    @Override
    public EndpointSpecification getEndpointSpecification()
    {
        return AccountServiceResolver.getListEndpointSpecification();
    }

    @DataProvider
    public static Object[][] testTransformQueryDataProvider()
    {
        return new Object[][]{
                {"", "", null, null},
                {"contextId==1001",
                        "(accountId==publisher/1001,accountId==seat/1001,accountId==partner/1001,accountId==mp-vendor/1001,accountId==streaming-seat/1001,accountId==streaming-buyer/1001)",
                        null, null},
                {"contextId=in=(1001,2763)",
                        "(accountId==publisher/1001,accountId==seat/1001,accountId==partner/1001,accountId==mp-vendor/1001,accountId==streaming-seat/1001,accountId==streaming-buyer/1001,accountId==publisher/2763,"
                        +
                        "accountId==seat/2763,accountId==partner/2763,accountId==mp-vendor/2763,accountId==streaming-seat/2763,accountId==streaming-buyer/2763)",
                        null, null},
                {"name!=QA*", "accountName!='QA*'", null, null},
                {"contextId>foo", null,
                        BadRequestException.class, "contextId does not support the operator =gt="},
                {"something==foo", null,
                        BadRequestException.class, "query cannot contain field something"},
                {"trouble=missingEquals", null,
                        BadRequestException.class, "Unable to process the supplied query: trouble=missingEquals."}
        };
    }

    @Test
    @UseDataProvider("testTransformQueryDataProvider")
    public void testTransformQuery(String query, String expectedResult, Class expectedErrorClass,
                                   String expectedErrorMessage)
    {
        doAdaptQuery(query, expectedResult, expectedErrorClass, expectedErrorMessage);
    }

    @DataProvider
    public static Object[][] testTransformSortDataProvider()
    {
        return new Object[][]{
                {"", "", null, null},
                {"id", "id", null, null},
                {"-id", "id:desc", null, null},
                {"name", "accountName", null, null},
                {"contextType", "accountId", null, null},
                {"contextId", "accountIdNumeric", null, null},
                {"status", "status", null, null},
                {"-name,id", "accountName:desc,id", null, null},
                {"gibberish", null, BadRequestException.class, "Invalid sort parameter attributes gibberish"},
        };
    }

    @Test
    @UseDataProvider("testTransformSortDataProvider")
    public void testTransformSort(String sort, String expectedResult, Class expectedErrorClass,
                                  String expectedErrorMessage)
    {
        doAdaptSort(sort, expectedResult, expectedErrorClass, expectedErrorMessage);
    }
}
