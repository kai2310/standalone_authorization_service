package com.rubicon.platform.authorization.service.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rubicon.platform.authorization.model.api.PagedResponse;
import com.rubicon.platform.authorization.model.api.idm.User;
import com.rubicon.platform.authorization.service.client.revv4.OAuth2AuthenticationClient;
import com.rubicon.platform.authorization.service.utils.Constants;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

public class IdmClient extends BaseClient
{
    private OAuth2AuthenticationClient authenticationClient;

    public IdmClient(String baseUrl,
                     OAuth2AuthenticationClient authenticationClient)
    {
        super(baseUrl);
        this.authenticationClient = authenticationClient;
    }

    public User getUserByEmail(String email)
    {
        logger.info("sending a request to IDM to get user by email: {}", email);
        String requestUrl = this.baseUrl.concat("api/v1/user/retrieve");

        String accessToken = this.authenticationClient.getAccessToken().getAccessToken();

        Map<String, Object> queryParamMap = new HashMap<>();
        queryParamMap.put(Constants.QUERY_PARAMETER_ACCESS_TOKEN, accessToken);
        queryParamMap.put("query", "email==".concat(email));
        requestUrl += appendQueryParams(queryParamMap);

        PagedResponse<User> response = getObject(requestUrl, new TypeReference<PagedResponse<User>>()
        {
        });

        User user = null;
        if (!CollectionUtils.isEmpty(response.getContent()))
        {
            user = response.getContent().get(0);
        }
        else
        {
            logger.info("no user was found with email: {}", email);
        }

        return user;
    }
}
