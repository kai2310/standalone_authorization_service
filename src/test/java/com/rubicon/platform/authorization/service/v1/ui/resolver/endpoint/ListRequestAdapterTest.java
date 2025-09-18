package com.rubicon.platform.authorization.service.v1.ui.resolver.endpoint;

import com.rubicon.platform.authorization.service.exception.BadRequestException;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecification;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecificationBuilder;
import com.rubicon.platform.authorization.service.v1.ui.adapter.converter.QueryArgumentConverter;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class ListRequestAdapterTest extends AbstractListRequestAdapterTest
{
    private static final String API_FIELD_1 = "apiField1";
    private static final String API_FIELD_2 = "apiField2";
    private static final String API_FIELD_3 = "apiField3";
    private static final String API_FIELD_4 = "apiField4";

    private static final String DATA_FIELD_1 = "dataField1";
    private static final String DATA_FIELD_2 = "dataField2";
    private static final String DATA_FIELD_3 = "dataField3";
    private static final String DATA_FIELD_4 = "dataField4";

    @Override
    public EndpointSpecification getEndpointSpecification()
    {
        return new EndpointSpecificationBuilder("general")

                .addFieldMapping(API_FIELD_1, DATA_FIELD_1)
                .addFieldMapping(API_FIELD_2, DATA_FIELD_2)
                .addFieldMapping(API_FIELD_3, DATA_FIELD_3)
                .addFieldMapping(API_FIELD_4, DATA_FIELD_4, new TestQueryArgumentConverter())

                .setValidQuery(API_FIELD_1, API_FIELD_2, API_FIELD_4) // apiField3 not queryable
                .setValidSort(API_FIELD_1, API_FIELD_3, API_FIELD_4) // apiField2 not sortable

                .build();
    }

    @DataProvider
    public static Object[][] testTransformQueryDataProvider()
    {
        return new Object[][]{
                {"", "", null, null},
                {API_FIELD_1+"==foo",
                        DATA_FIELD_1+"=='foo'", null, null},
                {API_FIELD_1+"==foo;"+API_FIELD_2+"=in=(1,2,3)",
                        "("+DATA_FIELD_1+"=='foo';"+DATA_FIELD_2+"=in=('1','2','3'))", null, null},
                {"("+API_FIELD_1+"=lt='foo';"+API_FIELD_2+"=gt=2),("+API_FIELD_4+"!=bar)",
                        "(("+DATA_FIELD_1+"=lt='foo';"+DATA_FIELD_2+"=gt='2'),"+DATA_FIELD_4+"!='*bar*')", null, null},
                {API_FIELD_3+"==foo", null,
                        BadRequestException.class, "query cannot contain field " + API_FIELD_3},
                {API_FIELD_4+"==foo",
                        DATA_FIELD_4+"=='*foo*'", null, null},
                {"something=nothing", null,
                        BadRequestException.class, "Unable to process the supplied query: something=nothing."}
        };
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
                {API_FIELD_1, DATA_FIELD_1, null, null},
                {"-"+API_FIELD_1, DATA_FIELD_1+":desc", null, null},
                {"-"+API_FIELD_1+","+API_FIELD_4, DATA_FIELD_1+":desc,"+DATA_FIELD_4, null, null},
                {API_FIELD_2, null, BadRequestException.class, "Invalid sort parameter attributes " + API_FIELD_2},
        };
    }

    @Test
    @UseDataProvider("testTransformSortDataProvider")
    public void testTransformSort(String sort, String expectedResult,
                                   Class expectedErrorClass, String expectedErrorMessage)
    {
        doAdaptSort(sort, expectedResult, expectedErrorClass, expectedErrorMessage);
    }

    class TestQueryArgumentConverter extends QueryArgumentConverter
    {
        @Override
        public String convertSingleArgument(String apiComparator, String apiArgument)
        {
            return "*" + apiArgument + "*";
        }
    }
}
