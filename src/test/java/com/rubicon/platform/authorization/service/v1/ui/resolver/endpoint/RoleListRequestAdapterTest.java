package com.rubicon.platform.authorization.service.v1.ui.resolver.endpoint;

import com.rubicon.platform.authorization.service.exception.BadRequestException;
import com.rubicon.platform.authorization.service.v1.ui.resolver.RoleServiceResolver;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecification;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class RoleListRequestAdapterTest extends AbstractListRequestAdapterTest
{
    @Override
    public EndpointSpecification getEndpointSpecification()
    {
        return RoleServiceResolver.getListEndpointSpecification();
    }

    @DataProvider
    public static Object[][] testTransformQueryDataProvider()
    {
        return new Object[][]{
                {"", "", null, null},
                {"id=in=(1,2,3)", "id=in=(1,2,3)", null, null},
                {"name!=arg*", "label!='arg*'", null, null},
                {"name>arg", "label=gt='arg'", null, null},
                {"name<arg", "label=lt='arg'", null, null},
                {"type==seller", "roleTypeId==2", null, null},
                {"type=in=(seller,internal)", "roleTypeId=in=(2,1)", null, null},
                {"type=out=(seller,internal)", "roleTypeId=out=(2,1)", null, null},
                {"type==seller;name!=arg*", "(roleTypeId==2;label!='arg*')", null, null},
                {"type==pants", null, BadRequestException.class, "'pants' is not a valid role type"},
                {"something==foo", null,
                        BadRequestException.class, "query cannot contain field something"},
                {"trouble=missingEquals", null,
                        BadRequestException.class, "Unable to process the supplied query: trouble=missingEquals."}        };
    }

    @Test
    @UseDataProvider("testTransformQueryDataProvider")
    public void testTransformQuery(String query, String expectedResult,
                                   Class expectedErrorClass, String expectedErrorMessage)
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
                {"name", "label", null, null},
                {"type", "roleTypeLabel", null, null},
                {"type,-name", "roleTypeLabel,label:desc", null, null},
                {"gibberish", null, BadRequestException.class, "Invalid sort parameter attributes gibberish"},
        };
    }

    @Test
    @UseDataProvider("testTransformSortDataProvider")
    public void testTransformSort(String sort, String expectedResult,
                                  Class expectedErrorClass, String expectedErrorMessage)
    {
        doAdaptSort(sort, expectedResult, expectedErrorClass, expectedErrorMessage);
    }
}
