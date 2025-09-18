package com.rubicon.platform.authorization.service.v1.ui.client.leftovers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.model.data.lfo.Authorization;
import com.rubicon.platform.authorization.service.client.revv4.OAuth2AccessToken;
import com.rubicon.platform.authorization.service.client.revv4.OAuth2AuthenticationClient;
import com.rubicon.platform.authorization.service.utils.Constants;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class LeftoversDataClientTest extends TestAbstract
{
    private LeftoverDataClient client;
    private OAuth2AuthenticationClient authenticationClient;

    @Before
    public void setup()
    {
        String token = RandomStringUtils.randomAlphanumeric(32);

        OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
        when(oAuth2AccessToken.getAccessToken()).thenReturn(token);

        authenticationClient = mock(OAuth2AuthenticationClient.class);
        doReturn(oAuth2AccessToken).when(authenticationClient).getAccessToken();

        client = spy(new LeftoverDataClient("baseUrl", authenticationClient));

        doReturn(null).when(client).deleteObject(anyString());
        doReturn(null).when(client).createObject(anyString(), any(EntityList.class), eq(EntityList.class));

    }

    @Test
    public void addAuthorizationEntryTest()
    {
        client.addAuthorizationEntry(USER_ID, AUTHORIZATION_RESOURCE_ID,
                Constants.REVV_LEFTOVERS_AUTHORIZATION_ACCOUNT_RESOURCE_TYPE);
    }

    @DataProvider
    public static Object[][] deletedAuthorizationEntryDataProvider()
    {
        return new Object[][]{
                {true}, {false}
        };
    }

    @Test
    @UseDataProvider("deletedAuthorizationEntryDataProvider")
    public void deletedAuthorizationEntryTest(boolean hasAuthorizationEntry)
    {
        EntityResponse<Authorization> entityResponse = new EntityResponse<>();
        if (hasAuthorizationEntry)
        {
            entityResponse.setContent(Arrays.asList(getAuthorization()));
        }

        doReturn(entityResponse).when(client).getObject(anyString(), any(TypeReference.class));

        client.deleteAuthorizationEntry(USER_ID, AUTHORIZATION_RESOURCE_ID,
                Constants.REVV_LEFTOVERS_AUTHORIZATION_ACCOUNT_RESOURCE_TYPE);

        if (hasAuthorizationEntry)
        {
            verify(client).deleteObject(anyString());
        }
    }

    @DataProvider
    public static Object[][] findMatchedAuthorizationDataProvider()
    {
        EntityResponse<Authorization> matchedResponse = new EntityResponse<>();
        matchedResponse.setContent(Collections.singletonList(getAuthorization()));

        EntityResponse<Authorization> unmatchedWildcardResponse = new EntityResponse<>();
        unmatchedWildcardResponse.setContent(Collections.singletonList(getAuthorization()));

        EntityResponse<Authorization> matchedWildcardResponse = new EntityResponse<>();
        Authorization wildcardAuth = getAuthorization();
        wildcardAuth.setResourceId(Constants.REVV_LEFTOVERS_AUTHORIZATION_WILDCARD_RESOURCE_ID);
        matchedWildcardResponse.setContent(Collections.singletonList(wildcardAuth));

        return new Object[][]{
                {AUTHORIZATION_RESOURCE_ID, null, false},
                {AUTHORIZATION_RESOURCE_ID, new EntityResponse<>(), false},
                {AUTHORIZATION_RESOURCE_ID, matchedResponse, true},
                {Constants.REVV_LEFTOVERS_AUTHORIZATION_WILDCARD_RESOURCE_ID, unmatchedWildcardResponse, false},
                {Constants.REVV_LEFTOVERS_AUTHORIZATION_WILDCARD_RESOURCE_ID, matchedWildcardResponse, true}
        };
    }

    @Test
    @UseDataProvider("findMatchedAuthorizationDataProvider")
    public void findMatchedAuthorizationTest(String resourceId, EntityResponse<Authorization> response,
                                             boolean hasContent)
    {
        assertEquals(hasContent, client.findMatchedAuthorization(response, resourceId) != null);
    }
}
