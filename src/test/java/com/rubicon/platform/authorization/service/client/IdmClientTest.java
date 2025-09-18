package com.rubicon.platform.authorization.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rubicon.platform.authorization.TestAbstract;
import com.rubicon.platform.authorization.model.api.PagedResponse;
import com.rubicon.platform.authorization.model.api.idm.User;
import com.rubicon.platform.authorization.service.client.revv4.OAuth2AccessToken;
import com.rubicon.platform.authorization.service.client.revv4.OAuth2AuthenticationClient;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.spy;

@RunWith(DataProviderRunner.class)
public class IdmClientTest extends TestAbstract
{
    private IdmClient idmClient;

    @Before
    public void setup()
    {
        OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
        when(oAuth2AccessToken.getAccessToken()).thenReturn(RandomStringUtils.randomAlphanumeric(32));

        OAuth2AuthenticationClient authenticationClient = mock(OAuth2AuthenticationClient.class);
        doReturn(oAuth2AccessToken).when(authenticationClient).getAccessToken();

        idmClient = spy(new IdmClient("baseUrl", authenticationClient));
    }

    @DataProvider
    public static Object[][] getUserByEmailDataProvider()
    {
        return new Object[][]{
                {true},
                {false}
        };
    }

    @Test
    @UseDataProvider("getUserByEmailDataProvider")
    public void getUserByEmailTest(boolean hasUser)
    {
        User user = new User();
        user.setId(Long.valueOf(USER_ID));
        user.setEmail(USER_EMAIL);

        List<User> userList = hasUser
                              ?
                              Collections.singletonList(user)
                              : new ArrayList<>();

        PagedResponse<User> response = new PagedResponse<>();
        response.setContent(userList);

        doReturn(response).when(idmClient).getObject(anyString(), any(TypeReference.class));

        User actual = idmClient.getUserByEmail(USER_EMAIL);

        assertEquals(hasUser, actual != null);
    }
}
