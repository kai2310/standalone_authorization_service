package com.rubicon.platform.authorization.service.v1.ui.resolver.endpoint;

import com.rubicon.platform.authorization.service.exception.BadRequestException;
import com.rubicon.platform.authorization.service.v1.ui.resolver.FeatureServiceResolver;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecification;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class FeatureListRequestAdapterTest extends AbstractListRequestAdapterTest
{
    @Override
    public EndpointSpecification getEndpointSpecification()
    {
        return FeatureServiceResolver.getListEndpointSpecification();
    }

    @DataProvider
    public static Object[][] testTransformQueryDataProvider()
    {
        return new Object[][]{
                {"", "", null, null},
                {"id=in=(1,2,3)", "id=in=(1,2,3)", null, null},
                {"name!=arg*", "label!='arg*'", null, null},
                {"name>=arg", "label=ge='arg'", null, null},
                {"name<=arg", "label=le='arg'", null, null},
                {"name==foo;id!=2", "(label=='foo';id!=2)", null, null},
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
                {"-name,id", "label:desc,id", null, null},
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
