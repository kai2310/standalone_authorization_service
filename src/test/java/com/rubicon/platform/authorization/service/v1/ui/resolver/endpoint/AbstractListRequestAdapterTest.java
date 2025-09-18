package com.rubicon.platform.authorization.service.v1.ui.resolver.endpoint;

import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.service.v1.ui.adapter.EndpointSpecification;
import com.rubicon.platform.authorization.service.v1.ui.adapter.ListRequestAdapter;
import org.junit.Before;

public abstract class AbstractListRequestAdapterTest extends TestAbstract
{
    protected ListRequestAdapter listRequestAdapter;

    public abstract EndpointSpecification getEndpointSpecification();

    @Before
    public void setup()
    {
        listRequestAdapter = new ListRequestAdapter(getEndpointSpecification(), false);
    }

    protected final void doAdaptQuery(String query, String expectedResult,
                                       Class expectedErrorClass, String expectedErrorMessage)
    {
        if (null != expectedErrorClass)
        {
            expectedException.expect(expectedErrorClass);
            expectedException.expectMessage(expectedErrorMessage);
        }

        String result = listRequestAdapter.adaptQuery(query);
        assertEquals(expectedResult, result);
    }

    protected final void doAdaptSort(String sort, String expectedResult,
                                      Class expectedErrorClass, String expectedErrorMessage)
    {
        if (null != expectedErrorClass)
        {
            expectedException.expect(expectedErrorClass);
            expectedException.expectMessage(expectedErrorMessage);
        }

        String result = listRequestAdapter.adaptSort(sort);
        assertEquals(expectedResult, result);
    }
}
